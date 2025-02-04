package ru.practicum.privateApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.general.dto.request.participation.ParticipationRequestDto;
import ru.practicum.privateApi.service.request.PrivateParticipationRequestService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateParticipationRequestController {
    private final PrivateParticipationRequestService service;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> saveParticipationRequest(@PathVariable("userId") Long userId,
                                                                            @RequestParam Long eventId) {
        log.debug("New POST request received. Participation request for event: event id={}",
                eventId);

        ParticipationRequestDto participationRequestDto = service.createParticipationRequest(eventId, userId);
        log.debug("Participation request successfully saved. Event id={}, user id={}",
                eventId,
                userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(participationRequestDto);
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getParticipationRequests(@PathVariable("userId") Long userId) {
        log.debug("New GET request received. Fetch all participation requests from user with id: {}", userId);

        List<ParticipationRequestDto> participationRequestDtos = service.getParticipationRequests(userId);
        if (participationRequestDtos.isEmpty()) {
            log.debug("No participation requests fetched. Return empty list");
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        } else {
            log.debug("Participation requests successfully fetched. Count: {}", participationRequestDtos.size());
            return ResponseEntity.status(HttpStatus.OK).body(participationRequestDtos);
        }
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelParticipationRequest(@PathVariable("userId") Long userId,
                                                                              @PathVariable("requestId") Long requestId) {
        log.debug("New PATCH request received. Cancel participation request. User id: {}, request id: {}", userId, requestId);
        ParticipationRequestDto participationRequestDto = service.cancelParticipationRequest(userId, requestId);
        log.debug("Participation request successfully canceled");
        return ResponseEntity.status(HttpStatus.OK).body(participationRequestDto);
    }
}