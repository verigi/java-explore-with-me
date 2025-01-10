package ru.practicum.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.service.event.AdminEventService;
import ru.practicum.common.dto.event.EventDto;
import ru.practicum.common.dto.event.update.UpdateEventAdminRequestDto;
import ru.practicum.common.dto.event.update.UpdateEventDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private final AdminEventService service;

    @GetMapping
    ResponseEntity<List<EventDto>> getFilteredEvents(@RequestParam(required = false) List<Long> users,
                                                     @RequestParam(required = false) List<String> states,
                                                     @RequestParam(required = false) List<Long> categories,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        log.debug("New GET request received. Parameters: users={}, states={}, categories={}, start={}, end={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        List<EventDto> eventDtos = service.getFilteredEvents(users, states, categories, rangeStart, rangeEnd, from, size);

        if (eventDtos.isEmpty()) {
            log.debug("No events fetched. Return empty list");
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        } else {
            log.debug("Events successfully fetched. Count: {}", eventDtos.size());
            return ResponseEntity.status(HttpStatus.OK).body(eventDtos);
        }
    }


    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable("eventId") Long eventId,
                                                @Valid @RequestBody UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        log.debug("New PATCH request received. Trying to update event. Event id: {}", eventId);
        //ex 404 NOT_FOUND
        //ex 409 CONFLICT
        EventDto eventDto = service.updateEvent(eventId, updateEventAdminRequestDto); // тут будет сервис
        log.debug("Event \"{}\" successfully updated", eventDto.getTitle());
        return ResponseEntity.status(HttpStatus.OK).body(eventDto);
    }
}