package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.InvalidDateTimeException;
import ru.practicum.service.StatisticsService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class StatisticsController {
    private final StatisticsService service;
    private final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> saveHit(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.debug("Controller received POST request. Trying to log hit: app={}, uri={}, ip={}, timestamp={}",
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp());
        EndpointHitDto responseDto = service.saveHit(endpointHitDto);
        log.debug("Ending POST request processing");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getHits(@RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
                                                      @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime end,
                                                      @RequestParam(required = false) List<String> uris,
                                                      @RequestParam(defaultValue = "false") Boolean unique) {
        validateDateTime(start, end);
        log.debug("Controller received GET request. Trying to get hits: start={}, end={}, uris={}, unique={}",
                start,
                end,
                uris == null ? Collections.emptyList() : uris,
                unique);
        List<ViewStatsDto> responseDto = service.getHits(start, end, uris, unique);
        log.debug("Ending GET request processing");
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    private void validateDateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            log.error("Invalid /stats request: 'start' references null");
            throw new InvalidDateTimeException("Invalid request: start must not be null");
        }
        if (end == null) {
            log.error("Invalid /stats request: 'end' references null");
            throw new InvalidDateTimeException("Invalid request: end must not be null");
        }
        if (!start.isBefore(end)) {
            log.error("Invalid /stats request: 'start' is after 'end'");
            throw new InvalidDateTimeException("Invalid request: start must not be before end");
        }
    }
}