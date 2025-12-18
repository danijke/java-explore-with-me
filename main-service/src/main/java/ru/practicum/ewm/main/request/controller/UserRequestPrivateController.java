package ru.practicum.ewm.main.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.request.dto.*;
import ru.practicum.ewm.main.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class UserRequestPrivateController {

    private final RequestService requestService;

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addParticipationRequest(@PathVariable Long userId,
                                                           @RequestParam Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipants(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return requestService.getEventParticipants(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.changeStatus(userId, eventId, request);
    }
}
