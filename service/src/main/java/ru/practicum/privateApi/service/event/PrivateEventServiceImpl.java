package ru.practicum.privateApi.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.general.dto.comment.CommentShortDto;
import ru.practicum.general.dto.event.CreateEventDto;
import ru.practicum.general.dto.event.EventDto;
import ru.practicum.general.dto.event.update.UpdateEventUserRequestDto;
import ru.practicum.general.dto.request.change.EventRequestStatusUpdateRequest;
import ru.practicum.general.dto.request.change.EventRequestStatusUpdateResult;
import ru.practicum.general.dto.request.participation.ParticipationRequestDto;
import ru.practicum.general.enums.StateRequest;
import ru.practicum.general.exceptions.CustomAccessException;
import ru.practicum.general.exceptions.CustomConflictException;
import ru.practicum.general.mapper.CommentMapper;
import ru.practicum.general.mapper.EventMapper;
import ru.practicum.general.mapper.ParticipationRequestMapper;
import ru.practicum.general.model.Category;
import ru.practicum.general.model.Event;
import ru.practicum.general.model.ParticipationRequest;
import ru.practicum.general.model.User;
import ru.practicum.general.repository.*;
import ru.practicum.general.util.StatisticsHandler;
import ru.practicum.general.util.EntityHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrivateEventServiceImpl implements PrivateEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final CommentRepository commentRepository;
    private final EventMapper eventMapper;
    private final ParticipationRequestMapper participationRequestMapper;
    private final CommentMapper commentMapper;
    private final EntityHandler entityHandler;
    private final StatisticsHandler statisticsHandler;

    @Autowired
    public PrivateEventServiceImpl(EventRepository eventRepository,
                                   CategoryRepository categoryRepository,
                                   UserRepository userRepository,
                                   ParticipationRequestRepository participationRequestRepository,
                                   CommentRepository commentRepository,
                                   EventMapper eventMapper,
                                   ParticipationRequestMapper participationRequestMapper,
                                   CommentMapper commentMapper,
                                   EntityHandler entityHandler,
                                   StatisticsHandler statisticsHandler) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.participationRequestRepository = participationRequestRepository;
        this.commentRepository = commentRepository;
        this.participationRequestMapper = participationRequestMapper;
        this.eventMapper = eventMapper;
        this.commentMapper = commentMapper;
        this.entityHandler = entityHandler;
        this.statisticsHandler = statisticsHandler;
    }

    @Override
    @Transactional
    public EventDto createEvent(Long userId, CreateEventDto createEventDto) {
        log.debug("Attempting request to create event from user id={}", userId);
        User user = entityHandler.findEntityById(userRepository, userId, "User");
        Category category = entityHandler.findEntityById(categoryRepository, createEventDto.getCategory(), "Category");
        entityHandler.validateUserEventDateToCreateAndUpdate(createEventDto.getEventDate());

        Event event = eventMapper.toEntity(createEventDto, category);
        event.setInitiator(user);
        eventRepository.save(event);

        log.debug("Event created. Id={}", event.getId());
        return eventMapper.toDto(event, 0, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getUserEvents(Long userId, int from, int size) {
        log.debug("Attempting request to get all events from user id={}", userId);
        Pageable pageable = PageRequest.of(from / size, size);
        User user = entityHandler.findEntityById(userRepository, userId, "User");

        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageable);
        List<Long> eventIds = events.stream().map(event -> event.getId()).collect(Collectors.toList());
        Map<Long, Long> viewsMap = statisticsHandler.getViews(eventIds);
        List<EventDto> eventDtos = events.stream()
                .map(event -> {
                    int views = statisticsHandler.extractViews(viewsMap, event.getId());

                    List<CommentShortDto> commentDtos = commentRepository.findAllByEvent_Id(event.getId()).stream()
                            .map(comment -> commentMapper.toShortDto(comment))
                            .collect(Collectors.toList());
                    return eventMapper.toDto(event, views, commentDtos);
                })
                .collect(Collectors.toList());

        log.debug("Events fetched. Size={}", eventDtos.size());
        return eventDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getUserEvent(Long userId, Long eventId) {
        log.debug("Attempting request to get event id={} from user id={}", eventId, userId);

        User user = entityHandler.findEntityById(userRepository, userId, "User");
        Event event = entityHandler.findEntityById(eventRepository, eventId, "Event");
        Map<Long, Long> viewsMap = statisticsHandler.getViews(List.of(eventId));
        int views = statisticsHandler.extractViews(viewsMap, eventId);

        List<CommentShortDto> commentDtos = commentRepository.findAllByEvent_Id(eventId).stream()
                .map(comment -> commentMapper.toShortDto(comment))
                .collect(Collectors.toList());

        EventDto eventDto = eventMapper.toDto(event, views, commentDtos);

        log.debug("Event fetched. Id={}", eventId);
        return eventDto;
    }


    @Override
    @Transactional
    public EventDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventUserRequestDto) {
        log.debug("Attempting request to update event id={} from user id={}", eventId, userId);

        User user = entityHandler.findEntityById(userRepository, userId, "User");
        Event event = entityHandler.findEntityById(eventRepository, eventId, "Event");
        entityHandler.validateUserEventStateToUpdate(event);
        if (updateEventUserRequestDto.getEventDate() != null) {
            entityHandler.validateUserEventDateToCreateAndUpdate(updateEventUserRequestDto.getEventDate());
        }
        event = eventMapper.updateEntity(event, updateEventUserRequestDto);
        eventRepository.flush();

        List<CommentShortDto> commentDtos = commentRepository.findAllByEvent_Id(eventId).stream()
                .map(comment -> commentMapper.toShortDto(comment))
                .collect(Collectors.toList());

        log.debug("Event updated. Id={}", eventId);
        return eventMapper.toDto(event, event.getViews(), commentDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getParticipationRequests(Long userId, Long eventId) {
        log.debug("Attempting to get all participation requests of event id={} from user id={}", eventId, userId);
        User user = entityHandler.findEntityById(userRepository, userId, "User");
        Event event = entityHandler.findEntityById(eventRepository, eventId, "Event");

        List<ParticipationRequest> participationRequests = event.getRequests();
        List<ParticipationRequestDto> participationRequestDtos = participationRequests.stream()
                .map(request -> participationRequestMapper.toDto(request))
                .collect(Collectors.toList());

        log.debug("Participation requests fetched. Size={}", participationRequestDtos.size());
        return participationRequestDtos;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeParticipationRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.debug("Attempting to change status of requests ids={}, of event id={}, from user id={}", eventRequestStatusUpdateRequest.getRequestIds(),
                eventId,
                userId);
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();

        User user = entityHandler.findEntityById(userRepository, userId, "User");
        Event event = entityHandler.findEntityById(eventRepository, eventId, "Event");
        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new CustomAccessException("Only initiator can change statuses of requests");
        }

        List<ParticipationRequest> requests = requestIds.stream()
                .map(id -> entityHandler.findEntityById(participationRequestRepository, id, "ParticipationRequest"))
                .collect(Collectors.toList());
        requests.forEach(request -> {
            if (!request.getEvent().getId().equals(event.getId())) {
                throw new CustomConflictException("Request with id " + request.getId() + " do not belong to event with id " + event.getId());
            }
            if (!request.getStatus().equals(StateRequest.PENDING)) {
                throw new CustomConflictException("Request must be in 'PENDING' status to change. Current status: " + request.getStatus());
            }
        });

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        if (eventRequestStatusUpdateRequest.getStatus().equals(StateRequest.CONFIRMED)) {
            long alreadyConfirmedCount = participationRequestRepository.countConfirmedRequests(eventId);
            if (event.getParticipantLimit() != 0 && alreadyConfirmedCount + requests.size() > event.getParticipantLimit()) {
                throw new CustomConflictException("Cannot confirm requests: participant limit exceeded");
            }

            for (ParticipationRequest request : requests) {
                request.setStatus(StateRequest.CONFIRMED);
                confirmedRequests.add(request);
            }

            if (event.getParticipantLimit() != 0 && alreadyConfirmedCount + requests.size() == event.getParticipantLimit()) {
                List<ParticipationRequest> stillPendingParticipationRequests = participationRequestRepository.findByEventAndStatus(eventId, StateRequest.PENDING);
                for (ParticipationRequest pendingRequest : stillPendingParticipationRequests) {
                    pendingRequest.setStatus(StateRequest.REJECTED);
                    rejectedRequests.add(pendingRequest);
                }
                participationRequestRepository.saveAll(rejectedRequests);
            }
        } else if (eventRequestStatusUpdateRequest.getStatus().equals(StateRequest.REJECTED)) {
            for (ParticipationRequest request : requests) {
                request.setStatus(StateRequest.REJECTED);
                rejectedRequests.add(request);
            }
        } else {
            throw new IllegalArgumentException("Unknown status: " + eventRequestStatusUpdateRequest.getStatus());
        }

        participationRequestRepository.saveAll(confirmedRequests);
        participationRequestRepository.saveAll(rejectedRequests);

        log.debug("Requests updated: confirmed={}, rejected={}", confirmedRequests.size(), rejectedRequests.size());

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream()
                        .map(request -> participationRequestMapper.toDto(request))
                        .collect(Collectors.toList()))
                .rejectedRequests(rejectedRequests.stream()
                        .map(request -> participationRequestMapper.toDto(request))
                        .collect(Collectors.toList()))
                .build();
    }


}