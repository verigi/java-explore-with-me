package ru.practicum.publicApi.service.event;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.general.dto.event.EventDto;
import ru.practicum.general.enums.StateEvent;
import ru.practicum.general.mapper.EventMapper;
import ru.practicum.general.model.Event;
import ru.practicum.general.repository.CategoryRepository;
import ru.practicum.general.repository.EventRepository;
import ru.practicum.general.util.StatisticsHandler;
import ru.practicum.general.util.ValidationHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final StatisticsHandler statisticsHandler;
    private final ValidationHandler validationHandler;
    @Value("${app}")
    String app;

    @Autowired
    public PublicEventServiceImpl(EventRepository eventRepository,
                                  CategoryRepository categoryRepository,
                                  EventMapper eventMapper,
                                  StatisticsHandler statisticsHandler,
                                  ValidationHandler validationHandler) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
        this.statisticsHandler = statisticsHandler;
        this.validationHandler = validationHandler;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getFilteredEvents(String text,
                                            List<Long> catIds,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            int from,
                                            int size,
                                            String sort,
                                            HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new IllegalArgumentException("Range start cannot be after range end");
        }
        Sort sortBy;
        switch (sort) {
            case "EVENT_DATE" -> {
                log.debug("Sorting: EVENT_DATE");
                sortBy = Sort.by(Sort.Order.asc("eventDate"));
            }
            case "VIEWS" -> {
                log.debug("Sorting: VIEWS");
                sortBy = Sort.by(Sort.Order.desc("views"));
            }
            default -> sortBy = Sort.unsorted();
        }

        Pageable pageable = PageRequest.of(from / size, size, sortBy);

        if (catIds == null || catIds.isEmpty()) {
            catIds = categoryRepository.findAll().stream()
                    .map(category -> category.getId())
                    .collect(Collectors.toList());
            log.debug("No category ids entered. Default value provided: {}", catIds);
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
            log.debug("No start time entered. Default value provided: {}", rangeStart);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(2100, 1, 1, 0, 0, 0);
            log.debug("No end time entered. Default value provided: {}", rangeEnd);
        }

        log.debug("Attempting to get events. Text={}, categories ids={}, paid={}, start={}, end={}, available={}, sort={}. Pagination: from={}, size={}",
                text, catIds, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        Page<Event> events = eventRepository.openFindFilteredEvents(text, catIds, paid, rangeStart, rangeEnd, onlyAvailable, pageable);
        if (events.isEmpty()) {
            log.debug("No events found for the given filters");
            return List.of();
        }
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statisticsHandler.saveHit(endpointHitDto);

        List<Long> eventIds = events.stream().map(event -> event.getId()).collect(Collectors.toList());
        Map<Long, Long> viewsMap = statisticsHandler.getViews(eventIds);


        return events.stream()
                .map(event -> {
                    int views = statisticsHandler.extractViews(viewsMap, event.getId());
                    return eventMapper.toDto(event, views);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEvent(Long eventId, HttpServletRequest request) {
        log.debug("Attempting to get event: id={}", eventId);
        Event event = validationHandler.findEntityById(eventRepository, eventId, "Event");
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new EntityNotFoundException("Denied to fetch event from public API: event is not published");
        }
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statisticsHandler.saveHit(endpointHitDto);

        Map<Long, Long> viewsMap = statisticsHandler.getViews(List.of(eventId));
        int views = statisticsHandler.extractViews(viewsMap, eventId);
        return eventMapper.toDto(event, views);
    }
}