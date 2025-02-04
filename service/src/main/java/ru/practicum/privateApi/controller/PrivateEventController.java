package ru.practicum.privateApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.general.dto.event.CreateEventDto;
import ru.practicum.general.dto.event.EventDto;
import ru.practicum.general.dto.event.update.UpdateEventUserRequestDto;
import ru.practicum.general.dto.request.change.EventRequestStatusUpdateRequest;
import ru.practicum.general.dto.request.change.EventRequestStatusUpdateResult;
import ru.practicum.general.dto.request.participation.ParticipationRequestDto;
import ru.practicum.privateApi.service.event.PrivateEventService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final PrivateEventService service;

    @PostMapping
    public ResponseEntity<EventDto> saveEvent(@PathVariable Long userId,
                                              @Valid @RequestBody CreateEventDto createEventDto) {
        log.debug("New POST request received. Event title: {}", createEventDto.getTitle());

        EventDto eventDto = service.createEvent(userId, createEventDto);
        log.debug("Event \"{}\" successfully saved", eventDto.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(eventDto);
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getEvents(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        log.debug("New GET request received. Fetch all events from user with id: {}", userId);

        List<EventDto> eventDtos = service.getUserEvents(userId, from, size);
        if (eventDtos.isEmpty()) {
            log.debug("No events fetched. Return empty list");
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        } else {
            log.debug("Events successfully fetched. Count: {}", eventDtos.size());
            return ResponseEntity.status(HttpStatus.OK).body(eventDtos);
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        log.debug("New GET request received. Fetch event with id: {}", eventId);

        EventDto eventDto = service.getUserEvent(userId, eventId);
        log.debug("Event \"{}\" successfully fetched", eventDto.getTitle());
        return ResponseEntity.status(HttpStatus.OK).body(eventDto);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long userId,
                                                @PathVariable Long eventId,
                                                @Valid @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto) {
        log.debug("New PATCH request received. Update event with id: {}", eventId);

        EventDto eventDto = service.updateEvent(userId, eventId, updateEventUserRequestDto);
        log.debug("Event \"{}\" successfully updated", eventDto.getTitle());
        return ResponseEntity.status(HttpStatus.OK).body(eventDto);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getParticipationRequestsOfEvent(@PathVariable Long userId,
                                                                                         @PathVariable Long eventId) {
        log.debug("New GET request received. Get all participation requests of event with id: {}", eventId);

        List<ParticipationRequestDto> participationRequestDtos = service.getParticipationRequests(userId, eventId);
        log.debug("Participation requests successfully fetched. Count: {}", participationRequestDtos.size());
        return ResponseEntity.status(HttpStatus.OK).body(participationRequestDtos);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> changeParticipationRequestsStatus(@PathVariable Long userId,
                                                                                            @PathVariable Long eventId,
                                                                                            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.debug("New PATCH request received. Update participation requests statuses. Event id={}, request ids={}",
                eventId,
                eventRequestStatusUpdateRequest.getRequestIds());

        EventRequestStatusUpdateResult result = service.changeParticipationRequestsStatus(userId, eventId, eventRequestStatusUpdateRequest);
        log.debug("Participation requests successfully updated. Confirmed count={}, rejected count={}",
                result.getConfirmedRequests().size(),
                result.getRejectedRequests().size());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


}