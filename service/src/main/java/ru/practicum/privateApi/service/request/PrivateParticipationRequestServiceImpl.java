package ru.practicum.privateApi.service.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.general.dto.request.participation.ParticipationRequestDto;
import ru.practicum.general.enums.StateRequest;
import ru.practicum.general.mapper.ParticipationRequestMapper;
import ru.practicum.general.model.Event;
import ru.practicum.general.model.ParticipationRequest;
import ru.practicum.general.model.User;
import ru.practicum.general.repository.EventRepository;
import ru.practicum.general.repository.ParticipationRequestRepository;
import ru.practicum.general.repository.UserRepository;
import ru.practicum.general.util.ValidationHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class PrivateParticipationRequestServiceImpl implements PrivateParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ValidationHandler validationHandler;

    @Autowired
    public PrivateParticipationRequestServiceImpl(ParticipationRequestRepository participationRequestRepository,
                                                  ParticipationRequestMapper participationRequestMapper,
                                                  EventRepository eventRepository,
                                                  UserRepository userRepository,
                                                  ValidationHandler validationHandler) {
        this.participationRequestRepository = participationRequestRepository;
        this.participationRequestMapper = participationRequestMapper;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.validationHandler = validationHandler;
    }


    @Override
    public ParticipationRequestDto createParticipationRequest(Long eventId, Long userId) {
        log.debug("Attempting to create participation request. Event id={}, requester id={}",
                eventId,
                userId);

        Event event = validationHandler.findEntityById(eventRepository, eventId, "Event");
        User user = validationHandler.findEntityById(userRepository, userId, "User");
        validationHandler.validateParticipationRequest(event, user);
        ParticipationRequest participationRequest = participationRequestMapper.toEntity(user, event);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(StateRequest.CONFIRMED);
        } else {
            participationRequest.setStatus(StateRequest.PENDING);
        }
        ParticipationRequest savedParticipationRequest = participationRequestRepository.save(participationRequest);

        log.debug("Participation request created: event={}, requester={}",
                savedParticipationRequest.getEvent().getId(),
                savedParticipationRequest.getRequester().getId());
        return participationRequestMapper.toDto(savedParticipationRequest);
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequests(Long userId) {
        log.debug("Attempting to fetch participation requests from user. User id={}", userId);
        User user = validationHandler.findEntityById(userRepository, userId, "User");
        List<ParticipationRequest> participationRequests = participationRequestRepository.findAllByRequester_Id(userId);
        if (participationRequests.isEmpty()) {
            log.debug("No participation requests found for the given user");
            return List.of();
        }
        List<ParticipationRequestDto> participationRequestDtos = participationRequests.stream()
                .map(participationRequest -> participationRequestMapper.toDto(participationRequest))
                .collect(Collectors.toList());
        return participationRequestDtos;
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId) {
        log.debug("Attempting to cancel participation requests from user. User id={}, event id={}", userId, requestId);
        User user = validationHandler.findEntityById(userRepository, userId, "User");
        ParticipationRequest participationRequest = validationHandler.findEntityById(participationRequestRepository,
                requestId, "ParticipationRequest");
        participationRequest.setStatus(StateRequest.CANCELED);
        ParticipationRequest canceledParticipationRequest = participationRequestRepository.save(participationRequest);

        return participationRequestMapper.toDto(canceledParticipationRequest);
    }

}