package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.exception.ClientRequestException;
import ru.practicum.exception.ServerUnavailableException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StatisticsClient extends BaseClient {
    @Autowired
    public StatisticsClient(@Value("${stats.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> saveHit(EndpointHitDto endpointHitDto) {
        log.debug("Client is going to send POST request to server. Trying to save hit: app={} to uri={} with ip={}",
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp());
        try {
            log.debug("POST request successfully send to server");
            return post("/hit", null, endpointHitDto);
        } catch (ResourceAccessException e) {
            log.error("Failed to connect with the server: {}", e.getMessage());
            throw new ServerUnavailableException("Unable to get connection to server. Try again later");
        } catch (RestClientException e) {
            log.error("Unexpected exception. Failed to send POST request: {}", e.getMessage());
            throw new ClientRequestException("Unexpected exception: UI layer");
        }
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.debug("Client is going to send GET request to server. Trying to get hits: start={}, end={}",
                start,
                end);
        Map<String, Object> params = Map.of("start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );

        try {
            log.debug("GET request successfully send to server");
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", params);
        } catch (ResourceAccessException e) {
            log.error("Failed to connect with the server: {}", e.getMessage());
            throw new ServerUnavailableException("Unable to get connection to server. Try again later");
        } catch (RestClientException e) {
            log.error("Unexpected exception. Failed to send GET request: {}", e.getMessage());
            throw new ClientRequestException("Unexpected exception: UI layer");
        }
    }
}