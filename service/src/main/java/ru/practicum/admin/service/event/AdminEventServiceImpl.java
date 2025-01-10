package ru.practicum.admin.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.StatisticsClient;
import ru.practicum.common.dto.event.EventDto;
import ru.practicum.common.dto.event.update.UpdateEventAdminRequestDto;
import ru.practicum.common.enums.EventState;
import ru.practicum.common.mapper.EventMapper;
import ru.practicum.common.model.Event;
import ru.practicum.common.repository.CategoryRepository;
import ru.practicum.common.repository.EventRepository;
import ru.practicum.common.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final StatisticsClient statisticsClient;

    @Autowired
    public AdminEventServiceImpl(EventRepository eventRepository,
                                 UserRepository userRepository,
                                 CategoryRepository categoryRepository,
                                 EventMapper eventMapper,
                                 StatisticsClient statisticsClient) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
        this.statisticsClient = statisticsClient;
    }

    @Override
    public List<EventDto> getFilteredEvents(List<Long> userIds,
                                            List<String> eventState,
                                            List<Long> catIds,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            int from,
                                            int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        if (eventState == null || eventState.isEmpty()) {
            eventState = List.of(
                    EventState.PENDING.toString(),
                    EventState.CANCELED.toString(),
                    EventState.PUBLISHED.toString());
            log.debug("No state entered. Default value provided: {}", eventState);
        }
        if (userIds == null || userIds.isEmpty()) {
            userIds = userRepository.findAll().stream()
                    .map(user -> user.getId())
                    .collect(Collectors.toList());
            log.debug("No user ids entered. Default value provided: {}", userIds);
        }
        if (catIds == null || catIds.isEmpty()) {
            catIds = categoryRepository.findAll().stream()
                    .map(category -> category.getId())
                    .collect(Collectors.toList());
            log.debug("No category ids entered. Default value provided: {}", catIds);
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.of(1970, 1, 1, 0, 0);
            log.debug("No start time entered. Default value provided: {}", rangeStart);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(2100, 1, 1, 0, 0);
            log.debug("No end time entered. Default value provided: {}", rangeEnd);
        }
        log.debug("Attempting to get events. User ids={}, state={}, categories ids={}, start={}, end={}",
                userIds, eventState, catIds, rangeStart, rangeEnd);

        List<EventState> states = Collections.emptyList();
        if (eventState != null) {
            try {
                states = eventState.stream()
                        .map(EventState::valueOf)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                log.error("Invalid event state provided: {}", eventState, e);
                throw new IllegalArgumentException("Invalid event state provided", e);
            }
        }

        List<Event> events = eventRepository.findFilteredEvents(userIds, states, catIds, rangeStart, rangeEnd, pageable);
        if (events.isEmpty()) {
            log.debug("No events found for the given filters");
            return List.of();
        }

        List<Long> eventIds = events.stream().map(event -> event.getId()).collect(Collectors.toList());
        Map<Long, Long> viewsMap = getViews(eventIds);

        return events.stream()
                .map(event -> {
                    int views = Math.toIntExact(viewsMap.getOrDefault(event.getId(), 0L));
                    return eventMapper.toDto(event, views);
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventDto updateEvent(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        log.debug("Attempting to update event. Target event id={}", eventId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Event with id {} does not exist", eventId);
            return new EntityNotFoundException("Event with id " + eventId + " does not exist");
        });


        Event eventUpd = eventMapper.updateEntity(event, updateEventAdminRequestDto);

        Map<Long, Long> viewsMap = getViews(List.of(eventUpd.getId()));
        int views = Math.toIntExact(viewsMap.getOrDefault(eventUpd.getId(), 0L));
        eventUpd.setViews(views);

        eventRepository.save(eventUpd);
        log.debug("Event successfully updated: id={}, title={}, views={}",
                eventUpd.getId(),
                eventUpd.getTitle(),
                views);
        return eventMapper.toDto(eventUpd, views);
    }

    private Map<Long, Long> getViews(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            log.debug("No event ids provided. Skip fetching views");
            return Collections.emptyMap();
        }
        log.debug("Connecting to statistics server. Fetching views for event ids: {}", eventIds);
        try {
            return statisticsClient.getViews(eventIds);
        } catch (Exception e) {
            log.debug("Fail to fetch views from statistics server");
            return Collections.emptyMap();
        }
    }
}