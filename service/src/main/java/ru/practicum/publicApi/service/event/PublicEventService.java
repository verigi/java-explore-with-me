package ru.practicum.publicApi.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.general.dto.event.EventDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {
    List<EventDto> getFilteredEvents(String text,
                                     List<Long> catIds,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Boolean onlyAvailable,
                                     int from,
                                     int size,
                                     String sort,
                                     HttpServletRequest request);

    EventDto getEvent(Long eventId, HttpServletRequest request);
}