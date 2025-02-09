package ru.practicum.privateApi.service.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.general.dto.request.participation.ParticipationRequestDto;
import ru.practicum.general.enums.StateRequest;
import ru.practicum.general.mapper.ParticipationRequestMapper;
import ru.practicum.general.model.Event;
import ru.practicum.general.model.ParticipationRequest;
import ru.practicum.general.model.User;
import ru.practicum.general.repository.EventRepository;
import ru.practicum.general.repository.ParticipationRequestRepository;
import ru.practicum.general.repository.UserRepository;
import ru.practicum.general.util.EntityHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrivateParticipationRequestServiceImpl implements PrivateParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EntityHandler entityHandler;

    @Autowired
    public PrivateParticipationRequestServiceImpl(ParticipationRequestRepository participationRequestRepository,
                                                  ParticipationRequestMapper participationRequestMapper,
                                                  EventRepository eventRepository,
                                                  UserRepository userRepository,
                                                  EntityHandler entityHandler) {
        this.participationRequestRepository = participationRequestRepository;
        this.participationRequestMapper = participationRequestMapper;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.entityHandler = entityHandler;
    }


    @Override
    @Transactional
    public ParticipationRequestDto createParticipationRequest(Long eventId, Long userId) {
        log.debug("Attempting to create participation request. Event id={}, requester id={}",
                eventId,
                userId);

        Event event = entityHandler.findEntityById(eventRepository, eventId, "Event");
        User user = entityHandler.findEntityById(userRepository, userId, "User");
        entityHandler.validateParticipationRequest(event, user);
        ParticipationRequest participationRequest = participationRequestMapper.toEntity(user, event);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(StateRequest.CONFIRMED);
        } else {
            participationRequest.setStatus(StateRequest.PENDING);
        }
        ParticipationRequest savedParticipationRequest = participationRequestRepository.save(participationRequest);

        log.debug("Participation request created. Event id={}, requester id={}",
                savedParticipationRequest.getEvent().getId(),
                savedParticipationRequest.getRequester().getId());
        return participationRequestMapper.toDto(savedParticipationRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getParticipationRequests(Long userId) {
        log.debug("Attempting to fetch participation requests from user. User id={}", userId);
        User user = entityHandler.findEntityById(userRepository, userId, "User");
        List<ParticipationRequest> participationRequests = participationRequestRepository.findAllByRequester_Id(userId);

        List<ParticipationRequestDto> participationRequestDtos = participationRequests.stream()
                .map(participationRequest -> participationRequestMapper.toDto(participationRequest))
                .collect(Collectors.toList());

        if (participationRequests.isEmpty()) {
            log.debug("No participation requests found for the given user");
        } else {
            log.debug("Participation requests fetched. Size={}", participationRequestDtos.size());
        }
        return participationRequestDtos;
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        log.debug("Attempting to cancel participation requests from user. User id={}, event id={}", userId, requestId);
        User user = entityHandler.findEntityById(userRepository, userId, "User");
        ParticipationRequest participationRequest = entityHandler.findEntityById(participationRequestRepository,
                requestId, "ParticipationRequest");
        participationRequest.setStatus(StateRequest.CANCELED);
        participationRequestRepository.flush();

        log.debug("Participation request canceled. Id={}", requestId);
        return participationRequestMapper.toDto(participationRequest);
    }
}