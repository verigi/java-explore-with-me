package ru.practicum.publicApi.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.general.dto.event.EventDto;
import ru.practicum.publicApi.service.event.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController {

    private final PublicEventService service;

    @GetMapping
    ResponseEntity<List<EventDto>> getFilteredEvents(@RequestParam(required = false) String text,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) Boolean paid,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                     @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
                                                     HttpServletRequest request) {
        log.debug("New GET request received. Parameters: text={}, categories={}, paid={}, start={}, end={}, onlyAvailable={}, from={}, size={}, sort={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size, sort);

        List<EventDto> eventDtos = service.getFilteredEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size, sort, request);

        if (eventDtos.isEmpty()) {
            log.debug("No events fetched. Return empty list");
        } else {
            log.debug("Events successfully fetched. Count: {}", eventDtos.size());
        }
        return ResponseEntity.status(HttpStatus.OK).body(eventDtos);
    }

    @GetMapping("/{eventId}")
    ResponseEntity<EventDto> getEvent(@PathVariable Long eventId,  HttpServletRequest request) {
        log.debug("New GET request received. Event id: {}", eventId);

        EventDto eventDto = service.getEvent(eventId, request);
        log.debug("Event successfully fetched");
        return ResponseEntity.status(HttpStatus.OK).body(eventDto);
    }
}