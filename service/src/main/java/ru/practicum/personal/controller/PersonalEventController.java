package ru.practicum.personal.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.event.CreateEventDto;
import ru.practicum.common.dto.event.EventDto;
import ru.practicum.common.model.Event;
import ru.practicum.personal.service.event.PersonalEventService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PersonalEventController {
    private final PersonalEventService service;

    @PostMapping
    public ResponseEntity<EventDto> saveEvent(@PathVariable Long userId,
                                              @Valid @RequestBody CreateEventDto createEventDto) {
        log.debug("New POST request received. Event title: {}", createEventDto.getTitle());

        EventDto eventDto = service.createEvent(userId, createEventDto);
        log.debug("Event \"{}\" successfully saved", eventDto.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventDto);
    }
}