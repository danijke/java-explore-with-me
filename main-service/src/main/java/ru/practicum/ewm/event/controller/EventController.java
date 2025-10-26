package ru.practicum.ewm.event.controller;

import ru.practicum.ewm.event.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService service;

    @GetMapping
    public void getEvents(HttpServletRequest request) {
        service.getEvents(request);
    }

    @GetMapping("/{id}")
    public void getEvents(@PathVariable Long id, HttpServletRequest request) {
        service.getEventById(id, request);
    }
}
