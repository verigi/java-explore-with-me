package ru.practicum.privateApi.service.request;

import ru.practicum.general.dto.request.participation.ParticipationRequestDto;

import java.util.List;

public interface PrivateParticipationRequestService {

    ParticipationRequestDto createParticipationRequest(Long eventId, Long userId);

    List<ParticipationRequestDto> getParticipationRequests(Long userId);

    ParticipationRequestDto cancelParticipationRequest(Long userId, Long requestId);
}