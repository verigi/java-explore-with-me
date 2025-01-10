package ru.practicum.admin.service.event;

import ru.practicum.common.dto.event.EventDto;
import ru.practicum.common.dto.event.update.UpdateEventAdminRequestDto;

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