package ru.practicum.ewm.main.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.commons.enums.*;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.*;
import ru.practicum.main.request.dto.*;
import ru.practicum.main.request.mapper.ParticipationRequestMapper;
import ru.practicum.main.request.model.ParticipationRequest;
import ru.practicum.main.request.repository.ParticipationRequestRepository;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RequestServiceImpl implements ru.practicum.main.request.service.RequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter isoFormatter;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        List<ParticipationRequest> list = requestRepository.findAllByRequesterId(userId);
        List<ParticipationRequestDto> result = new ArrayList<>();
        for (ParticipationRequest pr : list) {
            result.add(ParticipationRequestMapper.toDto(pr, isoFormatter));
        }
        return result;
    }

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot request participation in own event");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot participate in an unpublished event");
        }
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Duplicate participation request");
        }

        Integer limit = event.getParticipantLimit() == null ? 0 : event.getParticipantLimit();
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (limit != 0 && confirmed >= limit) {
            throw new ConflictException("The participant limit has been reached");
        }

        boolean preModeration = event.getRequestModeration() == null ? true : event.getRequestModeration();

        RequestStatus status;
        if (!preModeration || limit == 0) {
            status = RequestStatus.CONFIRMED;
        } else {
            status = RequestStatus.PENDING;
        }

        ParticipationRequest entity = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(status)
                .build();

        ParticipationRequest saved = requestRepository.save(entity);

        return ParticipationRequestMapper.toDto(saved, isoFormatter);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest pr = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        pr.setStatus(RequestStatus.CANCELED);
        ParticipationRequest saved = requestRepository.save(pr);
        return ParticipationRequestMapper.toDto(saved, isoFormatter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        List<ParticipationRequest> list = requestRepository.findAllByEventId(eventId);
        List<ParticipationRequestDto> result = new ArrayList<>();
        for (ParticipationRequest pr : list) {
            result.add(ParticipationRequestMapper.toDto(pr, isoFormatter));
        }
        return result;
    }

    @Override
    public EventRequestStatusUpdateResult changeStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        List<Long> ids = request.getRequestIds();
        if (ids == null || ids.isEmpty()) {
            return EventRequestStatusUpdateResult.builder()
                    .confirmedRequests(List.of())
                    .rejectedRequests(List.of())
                    .build();
        }

        List<ParticipationRequest> toUpdate = requestRepository.findAllByIdIn(ids);

        for (ParticipationRequest pr : toUpdate) {
            if (!pr.getEvent().getId().equals(eventId)) {
                throw new NotFoundException("Request with id=" + pr.getId() + " was not found");
            }
            if (pr.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Request must have status PENDING");
            }
        }

        Integer limit = event.getParticipantLimit() == null ? 0 : event.getParticipantLimit();
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        long capacityLeft = (limit == 0) ? Long.MAX_VALUE : (limit - confirmed);

        List<ParticipationRequestDto> confirmedDtos = new ArrayList<>();
        List<ParticipationRequestDto> rejectedDtos = new ArrayList<>();

        if (request.getStatus() == RequestUpdateStatus.CONFIRMED) {
            List<ParticipationRequest> toConfirm = new ArrayList<>();
            for (ParticipationRequest pr : toUpdate) {
                if (capacityLeft <= 0) {
                    throw new ConflictException("The participant limit has been reached");
                }
                pr.setStatus(RequestStatus.CONFIRMED);
                toConfirm.add(pr);
                capacityLeft--;
            }

            List<ParticipationRequest> savedConfirmed = requestRepository.saveAll(toConfirm);
            for (ParticipationRequest pr : savedConfirmed) {
                confirmedDtos.add(ParticipationRequestMapper.toDto(pr, isoFormatter));
            }

            if (limit != 0 && capacityLeft == 0) {
                List<ParticipationRequest> pendings = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
                if (!pendings.isEmpty()) {
                    for (ParticipationRequest pending : pendings) {
                        pending.setStatus(RequestStatus.REJECTED);
                    }
                    List<ParticipationRequest> savedRejected = requestRepository.saveAll(pendings);
                    for (ParticipationRequest pr : savedRejected) {
                        rejectedDtos.add(ParticipationRequestMapper.toDto(pr, isoFormatter));
                    }
                }
            }
        } else if (request.getStatus() == RequestUpdateStatus.REJECTED) {
            for (ParticipationRequest pr : toUpdate) {
                pr.setStatus(RequestStatus.REJECTED);
            }
            List<ParticipationRequest> savedRejected = requestRepository.saveAll(toUpdate);
            for (ParticipationRequest pr : savedRejected) {
                rejectedDtos.add(ParticipationRequestMapper.toDto(pr, isoFormatter));
            }
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedDtos)
                .rejectedRequests(rejectedDtos)
                .build();
    }
}
