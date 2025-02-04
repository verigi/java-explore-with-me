package ru.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.DatabaseEndpointHitException;
import ru.practicum.exception.InvalidEndpointHitException;
import ru.practicum.exception.ServerProcessingException;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticRepository repository;

    @Override
    @Transactional
    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        validateEndpointHitDto(endpointHitDto);

        log.debug("Save hit from app={} to uri={} with ip={}",
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp());

        EndpointHit endpointHit = EndpointHitMapper.endpointHitFromDto(endpointHitDto);

        try {
            repository.save(endpointHit);
            log.debug("Successful saving. Return DTO to controller");
            return EndpointHitMapper.endpointHitToDto(endpointHit);
        } catch (DataIntegrityViolationException e) {
            logSaveHitError("Data integrity violation exception", e, endpointHitDto);
            throw new DatabaseEndpointHitException("Failed to save endpoint hit: invalid data");
        } catch (RuntimeException e) {
            logSaveHitError("Unexpected runtime exception during saving", e, endpointHitDto);
            throw new ServerProcessingException("Unexpected exception: service layer");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String choice = unique ? "unique" : "common";
        try {
            log.debug("Getting {} hits: start={}, end={}, uris={}", choice, start, end, uris);
            return collectHits(start, end, uris, unique);
        } catch (DataAccessException e) {
            logGetHitError("Data access exception", e, start, end, uris);
            throw new DatabaseEndpointHitException("Failed to get hits from database");
        } catch (RuntimeException e) {
            logGetHitError("Unexpected runtime exception during getting stats", e, start, end, uris);
            throw new ServerProcessingException("Unexpected exception: service layer");
        }
    }

    private void validateEndpointHitDto(EndpointHitDto endpointHitDto) {
        if (endpointHitDto == null) {
            log.error("Invalid EndpointHitDto: endpointHitDto references null");
            throw new InvalidEndpointHitException("Invalid EndpointHitDto: endpointHitDto must not null");
        }
        List<String> errors = new ArrayList<>();
        if (endpointHitDto.getApp() == null) {
            errors.add("'app'");
        }
        if (endpointHitDto.getUri() == null) {
            errors.add("'uri'");
        }
        if (endpointHitDto.getIp() == null) {
            errors.add("'ip'");
        }
        if (endpointHitDto.getTimestamp() == null) {
            errors.add("'timestamp'");
        }
        if (!errors.isEmpty()) {
            String msg = "Fields must not be null: " + String.join(", ", errors);
            log.error("Validation failed. {}", msg);
            throw new InvalidEndpointHitException(msg);
        }
    }

    private List<ViewStatsDto> collectHits(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.debug("Collecting hits. Get {} hits from DB", Boolean.TRUE.equals(unique) ? "unique" : "common");
        return unique ?
                repository.getUniqueHits(start, end, uris) :
                repository.getHits(start, end, uris);
    }

    private void logSaveHitError(String msg, Exception e, EndpointHitDto endpointHitDto) {
        log.error("{}: app={}, uri={}, ip={}, timestamp={}. Cause={}",
                msg,
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp(),
                e.getCause() != null ? e.getCause().getMessage() : "No cause provided");
    }

    private void logGetHitError(String msg, Exception e, LocalDateTime start, LocalDateTime end, List<String> uris) {
        log.error("{}: start={}, end={}, uris={}. Cause={}",
                msg,
                start,
                end,
                uris,
                e.getCause() != null ? e.getCause().getMessage() : "No cause provided");
    }
}