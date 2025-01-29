package ru.practicum.general.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import ru.practicum.StatisticsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.exception.ClientRequestException;
import ru.practicum.exception.ServerUnavailableException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StatisticsHandler {
    private final StatisticsClient statisticsClient;

    @Autowired
    public StatisticsHandler(StatisticsClient statisticsClient) {
        this.statisticsClient = statisticsClient;
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getViews(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            log.debug("No event ids provided. Skip fetching views");
            return Collections.emptyMap();
        }
        log.debug("Connecting to statistics server. Fetching views for event ids: {}", eventIds);
        try {
            Map<Long, Long> views = getViewsMap(eventIds);
            log.debug("Fetched views for {} events", views.size());
            return views;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.warn("Fail to fetch views from statistics server: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    @Transactional(readOnly = true)
    public int extractViews(Map<Long, Long> viewsMap, Long eventId) {
        return Math.toIntExact(viewsMap.getOrDefault(eventId, 0L));
    }

    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        log.debug("Connecting to statistics server. Saving endpoint hit: app={}, uri={}, ip={}",
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp());
        try {
            statisticsClient.saveHit(endpointHitDto);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.warn("Fail to save endpoint hit to statistics server: {}", e.getMessage());
        }
    }

    private Map<Long, Long> getViewsMap(List<Long> eventIds) {
        log.debug("Fetching view statistics for events: {}", eventIds);
        try {
            List<String> uris = buildUris(eventIds);

            ResponseEntity<Object> response = statisticsClient.getStats(
                    LocalDateTime.now().minusYears(1),
                    LocalDateTime.now().plusYears(1),
                    uris,
                    true
            );
            log.debug("Response from stats server: Status={}, Body={}", response.getStatusCode(), response.getBody());


            List<Map<String, Object>> responseMap = (List<Map<String, Object>>) response.getBody();
            if (responseMap == null || responseMap.isEmpty()) {
                log.warn("Stats server returned an empty response.");
                return eventIds.stream().collect(Collectors.toMap(id -> id, id -> 0L));
            }

            return responseMap.stream()
                    .filter(entry -> entry.containsKey("uri") && entry.containsKey("hits"))
                    .collect(Collectors.toMap(
                            entry -> extractEventIdFromUri(entry.get("uri").toString()),
                            entry -> Long.parseLong(entry.get("hits").toString())
                    ));
        } catch (ResourceAccessException e) {
            log.error("Failed to connect to stats server: {}", e.getMessage());
            throw new ServerUnavailableException("Unable to connect to stats server. Please try again later.");
        } catch (RestClientException e) {
            log.error("Failed to fetch views from stats server: {}", e.getMessage());
            throw new ClientRequestException("Unexpected error while fetching views from stats server.");
        } catch (Exception e) {
            log.error("Unexpected error while fetching views: {}", e.getMessage());
            return eventIds.stream().collect(Collectors.toMap(id -> id, id -> 0L));
        }
    }


    private List<String> buildUris(List<Long> ids) {
        return new ArrayList<>(ids.stream()
                .map(id -> "/events/" + id)
                .collect(Collectors.toList()));
    }

    private Long extractEventIdFromUri(String uri) {
        try {
            return Long.parseLong(uri.split("/")[2]);
        } catch (Exception e) {
            log.error("Failed to extract event id from URI: {}", uri);
            throw new IllegalArgumentException("Invalid URI format: " + uri);
        }
    }

}