package ru.practicum.ewm.main.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.commons.enums.RequestStatus;
import ru.practicum.ewm.main.compilation.dto.*;
import ru.practicum.ewm.main.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.main.compilation.model.Compilation;
import ru.practicum.ewm.main.compilation.repository.CompilationRepository;
import ru.practicum.ewm.main.compilation.service.CompilationService;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.repository.EventRepository;
import ru.practicum.ewm.main.exception.*;
import ru.practicum.ewm.main.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.main.stats.StatsService;
import ru.practicum.ewm.main.util.PageUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final StatsService statsService;
    private final DateTimeFormatter apiDateTimeFormatter;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Page<Compilation> page;
        if (pinned == null) {
            page = compilationRepository.findAll(PageUtils.offset(from, size));
        } else {
            page = compilationRepository.findAllByPinned(pinned, PageUtils.offset(from, size));
        }
        List<Compilation> comps = page.getContent();
        return mapWithAggregates(comps);
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {
        Compilation comp = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        List<Compilation> single = new ArrayList<>();
        single.add(comp);
        List<CompilationDto> list = mapWithAggregates(single);
        return list.getFirst();
    }

    @Override
    public CompilationDto create(NewCompilationDto dto) {
        if (compilationRepository.existsByTitleIgnoreCase(dto.getTitle())) {
            throw new ConflictException("Compilation title must be unique");
        }

        Compilation entity = new Compilation();
        entity.setTitle(dto.getTitle());
        entity.setPinned(dto.getPinned() != null ? dto.getPinned() : Boolean.FALSE);

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            Set<Long> ids = new HashSet<>(dto.getEvents());
            List<Event> events = eventRepository.findAllById(ids);
            entity.setEvents(new LinkedHashSet<>(events));
        } else {
            entity.setEvents(new LinkedHashSet<>());
        }

        Compilation saved = compilationRepository.save(entity);
        return getById(saved.getId());
    }

    @Override
    public void delete(Long compId) {
        Compilation comp = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        compilationRepository.delete(comp);
    }

    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        Compilation comp = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (dto.getTitle() != null) {
            String newTitle = dto.getTitle();
            if (!newTitle.equalsIgnoreCase(comp.getTitle())
                    && compilationRepository.existsByTitleIgnoreCase(newTitle)) {
                throw new ConflictException("Compilation title must be unique");
            }
            comp.setTitle(newTitle);
        }

        if (dto.getPinned() != null) {
            comp.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null) {
            if (dto.getEvents().isEmpty()) {
                comp.getEvents().clear();
            } else {
                Set<Long> ids = new HashSet<>(dto.getEvents());
                List<Event> events = eventRepository.findAllById(ids);
                comp.setEvents(new LinkedHashSet<>(events));
            }
        }

        Compilation saved = compilationRepository.save(comp);
        return getById(saved.getId());
    }

    private List<CompilationDto> mapWithAggregates(List<Compilation> comps) {
        Set<Long> allEventIds = new java.util.LinkedHashSet<>();
        for (Compilation c : comps) {
            for (Event e : c.getEvents()) {
                allEventIds.add(e.getId());
            }
        }

        Map<Long, Long> confirmed = new HashMap<>();
        Map<Long, Long> views = new HashMap<>();

        if (!allEventIds.isEmpty()) {
            List<Object[]> rows = requestRepository.countConfirmedByEventIds(allEventIds, RequestStatus.CONFIRMED);
            for (Object[] row : rows) {
                Long eventId = (Long) row[0];
                Long cnt = (Long) row[1];
                confirmed.put(eventId, cnt == null ? 0L : cnt);
            }

            List<String> uris = allEventIds.stream()
                    .map(id -> "/events/" + id)
                    .collect(Collectors.toList());
            Map<String, Long> vmap = statsService.getViews(uris);
            for (Long id : allEventIds) {
                Long v = vmap.getOrDefault("/events/" + id, 0L);
                views.put(id, v);
            }
        }

        List<CompilationDto> result = new ArrayList<>();
        for (Compilation c : comps) {
            result.add(CompilationMapper.toDto(c, confirmed, views, apiDateTimeFormatter));
        }
        return result;
    }
}
