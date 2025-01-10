package ru.practicum.common.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.common.enums.EventState;
import ru.practicum.common.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e " +
            "WHERE (:userIds IS NULL OR e.initiator.id IN :userIds) " +
            "AND (:eventState IS NULL OR e.state IS NULL OR e.state IN :eventState) " +
            "AND (:catIds IS NULL OR e.category.id IN :catIds) " +
            "AND (COALESCE(:rangeStart, e.eventDate) <= e.eventDate) " +
            "AND (COALESCE(:rangeEnd, e.eventDate) >= e.eventDate)")
    List<Event> findFilteredEvents(@Param("userIds") List<Long> userIds,
                                   @Param("eventState") List<EventState> eventState,
                                   @Param("catIds") List<Long> catIds,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   Pageable pageable);
}