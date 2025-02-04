package ru.practicum.adminApi.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.practicum.general.dto.event.EventDto;
import ru.practicum.general.dto.event.update.UpdateEventAdminRequestDto;
import ru.practicum.general.enums.StateEvent;
import ru.practicum.general.exceptions.InvalidStateException;
import ru.practicum.general.mapper.EventMapper;
import ru.practicum.general.model.Category;
import ru.practicum.general.model.Event;
import ru.practicum.general.model.User;
import ru.practicum.general.repository.CategoryRepository;
import ru.practicum.general.repository.EventRepository;
import ru.practicum.general.repository.UserRepository;
import ru.practicum.general.util.StatisticsHandler;
import ru.practicum.general.util.ValidationHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final ValidationHandler validationHandler;
    private final StatisticsHandler statisticsHandler;

    @Autowired
    public AdminEventServiceImpl(EventRepository eventRepository,
                                 UserRepository userRepository,
                                 CategoryRepository categoryRepository,
                                 EventMapper eventMapper,
                                 ValidationHandler validationHandler,
                                 StatisticsHandler statisticsHandler) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.eventMapper = eventMapper;
        this.validationHandler = validationHandler;
        this.statisticsHandler = statisticsHandler;
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
                    StateEvent.PENDING.toString(),
                    StateEvent.CANCELED.toString(),
                    StateEvent.PUBLISHED.toString());
            log.debug("No state entered. Default value provided: {}", eventState);
        }

        if (userIds == null || userIds.isEmpty()) {
            userIds = userRepository.findAll().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            log.debug("No user ids entered. Default value provided: {}", userIds);
        }

        if (catIds == null || catIds.isEmpty()) {
            catIds = categoryRepository.findAll().stream()
                    .map(Category::getId)
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

        log.debug("Attempting to get events. User ids={}, state={}, categories ids={}, start={}, end={}. Pagination: from={}, size={}",
                userIds, eventState, catIds, rangeStart, rangeEnd, from, size);

        List<StateEvent> states = parseStates(eventState);

        Page<Event> events = eventRepository.adminFindFilteredEvents(userIds, states, catIds, rangeStart, rangeEnd, pageable);
        if (events.isEmpty()) {
            log.debug("No events found for the given filters");
            return List.of();
        }

        return convertEventsToDtos(events);
    }

    @Override
    public EventDto updateEvent(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        log.debug("Updating event: id={}", eventId);

        Event event = validationHandler.findEntityById(eventRepository, eventId, "Event");

        validationHandler.validateAdminEventDate(event, updateEventAdminRequestDto.getEventDate());
        validationHandler.validateAdminEventState(event, updateEventAdminRequestDto);

        Event updatedEvent = eventMapper.updateEntity(event, updateEventAdminRequestDto);
        updatedEvent.setViews(statisticsHandler.extractViews(statisticsHandler.getViews(List.of(eventId)), eventId));

        eventRepository.save(updatedEvent);
        log.debug("Event updated: id={}, title={}", updatedEvent.getId(), updatedEvent.getTitle());

        return eventMapper.toDto(updatedEvent, updatedEvent.getViews());
    }

    private List<StateEvent> parseStates(List<String> eventState) {
        try {
            return eventState.stream()
                    .map(StateEvent::valueOf)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new InvalidStateException("Invalid event state provided");
        }
    }

    private List<EventDto> convertEventsToDtos(Page<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> viewsMap = statisticsHandler.getViews(eventIds);

        return events.stream()
                .map(event -> {
                    int views = statisticsHandler.extractViews(viewsMap, event.getId());
                    return eventMapper.toDto(event, views);
                })
                .collect(Collectors.toList());
    }
}