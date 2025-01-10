package ru.practicum.personal.service.event;

import ru.practicum.common.dto.event.CreateEventDto;
import ru.practicum.common.dto.event.EventDto;
import ru.practicum.common.dto.event.update.UpdateEventUserRequestDto;

import java.util.List;

public interface PersonalEventService {
    List<EventDto> getUserEvents(Long userId, int from, int size);

    EventDto getUserEvent(Long userId, Long eventId);

    EventDto createEvent(Long userId, CreateEventDto createEventDto);

    EventDto updateEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventAdminRequestDto);

    EventDto cancelEvent(Long userId, Long eventId);
}