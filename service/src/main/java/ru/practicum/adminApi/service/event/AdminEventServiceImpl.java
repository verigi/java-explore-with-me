package ru.practicum.adminApi.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.general.dto.comment.CommentShortDto;
import ru.practicum.general.dto.event.EventDto;
import ru.practicum.general.dto.event.update.UpdateEventAdminRequestDto;
import ru.practicum.general.enums.StateEvent;
import ru.practicum.general.mapper.CommentMapper;
import ru.practicum.general.mapper.EventMapper;
import ru.practicum.general.model.Category;
import ru.practicum.general.model.Event;
import ru.practicum.general.model.User;
import ru.practicum.general.repository.CategoryRepository;
import ru.practicum.general.repository.CommentRepository;
import ru.practicum.general.repository.EventRepository;
import ru.practicum.general.repository.UserRepository;
import ru.practicum.general.util.StatisticsHandler;
import ru.practicum.general.util.EntityHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final EventMapper eventMapper;
    private final CommentMapper commentMapper;
    private final EntityHandler entityHandler;
    private final StatisticsHandler statisticsHandler;

    @Autowired
    public AdminEventServiceImpl(EventRepository eventRepository,
                                 UserRepository userRepository,
                                 CategoryRepository categoryRepository,
                                 CommentRepository commentRepository,
                                 EventMapper eventMapper,
                                 CommentMapper commentMapper,
                                 EntityHandler entityHandler,
                                 StatisticsHandler statisticsHandler) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.eventMapper = eventMapper;
        this.commentMapper = commentMapper;
        this.entityHandler = entityHandler;
        this.statisticsHandler = statisticsHandler;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getFilteredEvents(List<Long> userIds,
                                            List<String> stateEvent,
                                            List<Long> catIds,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            int from,
                                            int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        if (stateEvent == null || stateEvent.isEmpty()) {
            stateEvent = List.of(
                    StateEvent.PENDING.toString(),
                    StateEvent.CANCELED.toString(),
                    StateEvent.PUBLISHED.toString());
            log.debug("No state entered. Default value provided: {}", stateEvent);
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
                userIds, stateEvent, catIds, rangeStart, rangeEnd, from, size);

        List<StateEvent> states = entityHandler.parseStates(stateEvent, StateEvent.class);

        Page<Event> events = eventRepository.adminFindFilteredEvents(userIds, states, catIds, rangeStart, rangeEnd, pageable);
        if (events.isEmpty()) {
            log.debug("No events found for the given filters");
        } else {
            log.debug("Events fetched. Size={}", events.getSize());
        }

        return convertEventsToDtos(events);
    }

    @Override
    @Transactional
    public EventDto updateEvent(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        log.debug("Updating event. Id={}", eventId);

        Event event = entityHandler.findEntityById(eventRepository, eventId, "Event");

        entityHandler.validateAdminEventDate(event, updateEventAdminRequestDto.getEventDate());
        entityHandler.validateAdminEventState(event, updateEventAdminRequestDto);

        event = eventMapper.updateEntity(event, updateEventAdminRequestDto);
        event.setViews(statisticsHandler.extractViews(statisticsHandler.getViews(List.of(eventId)), eventId));
        eventRepository.flush();

        List<CommentShortDto> commentDtos = commentRepository.findAllByEvent_Id(eventId).stream()
                .map(comment -> commentMapper.toShortDto(comment))
                .collect(Collectors.toList());

        log.debug("Event updated. Id={}", event.getId());
        return eventMapper.toDto(event, event.getViews(), commentDtos);
    }

    private List<EventDto> convertEventsToDtos(Page<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> viewsMap = statisticsHandler.getViews(eventIds);

        return events.stream()
                .map(event -> {
                    int views = statisticsHandler.extractViews(viewsMap, event.getId());

                    List<CommentShortDto> commentDtos = commentRepository.findAllByEvent_Id(event.getId()).stream()
                            .map(comment -> commentMapper.toShortDto(comment))
                            .collect(Collectors.toList());
                    return eventMapper.toDto(event, views, commentDtos);
                })
                .collect(Collectors.toList());
    }
}