package ru.practicum.general.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.general.enums.StateEvent;
import ru.practicum.general.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {


    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id IN ?1 " +
            "AND e.state IN ?2 " +
            "AND e.category.id IN ?3 " +
            "AND e.eventDate BETWEEN ?4 AND ?5")
    Page<Event> adminFindFilteredEvents(List<Long> userIds,
                                        List<StateEvent> stateEvent,
                                        List<Long> catIds,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (LOWER(e.annotation) LIKE CONCAT('%', LOWER(?1), '%') " +
            "OR LOWER(e.description) LIKE CONCAT('%', LOWER(?1), '%') OR ?1 IS NULL) " +
            "AND (e.category.id IN ?2) " +
            "AND (e.paid = ?3 OR ?3 IS NULL) " +
            "AND (e.eventDate >= ?4) " +
            "AND (e.eventDate <= ?5) " +
            "AND (?6 IS NULL OR e.participantLimit > (SELECT COUNT(pr) FROM ParticipationRequest pr " +
            "WHERE pr.event.id = e.id AND pr.status = 'CONFIRMED'))")
    Page<Event> openFindFilteredEvents(String text,
                                       List<Long> catIds,
                                       Boolean paid,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Boolean onlyAvailable,
                                       Pageable pageable);

    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    List<Event> findByCategory_Id(Long categoryId);
}