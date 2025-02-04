package ru.practicum.general.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.general.enums.StateRequest;
import ru.practicum.general.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("SELECT CASE WHEN COUNT(pr) > 0 THEN TRUE ELSE FALSE END " +
            "FROM ParticipationRequest pr WHERE pr.event.id = ?1 AND pr.requester.id = ?2")
    boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    @Query("SELECT COUNT(pr) FROM ParticipationRequest pr WHERE pr.event.id = ?1 AND pr.status = 'CONFIRMED'")
    long countConfirmedRequests(Long eventId);

    @Query("SELECT pr FROM ParticipationRequest pr " +
            "WHERE pr.event.id = ?1 " +
            "AND pr.status = ?2")
    List<ParticipationRequest> findByEventAndStatus(Long eventId, StateRequest stateRequest);

    List<ParticipationRequest> findAllByRequester_Id(Long userId);


}