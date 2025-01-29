package ru.practicum.adminApi.service.event;

import ru.practicum.general.dto.event.EventDto;
import ru.practicum.general.dto.event.update.UpdateEventAdminRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
    List<EventDto> getFilteredEvents(List<Long> userIds,
                                     List<String> eventState,
                                     List<Long> catIds,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     int from,
                                     int size);

    EventDto updateEvent(Long eventId,
                         UpdateEventAdminRequestDto updateEventAdminRequestDto);
}