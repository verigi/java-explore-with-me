package ru.practicum.privateApi.service.event;

import ru.practicum.general.dto.event.CreateEventDto;
import ru.practicum.general.dto.event.EventDto;
import ru.practicum.general.dto.event.update.UpdateEventUserRequestDto;
import ru.practicum.general.dto.request.change.EventRequestStatusUpdateRequest;
import ru.practicum.general.dto.request.change.EventRequestStatusUpdateResult;
import ru.practicum.general.dto.request.participation.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
    List<EventDto> getUserEvents(Long userId, int from, int size);

    EventDto getUserEvent(Long userId, Long eventId);

    EventDto createEvent(Long userId, CreateEventDto createEventDto);

    EventDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventAdminRequestDto);

    List<ParticipationRequestDto> getParticipationRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeParticipationRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest);
}