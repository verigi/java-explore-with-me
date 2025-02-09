package ru.practicum.general.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.general.enums.StateComment;
import ru.practicum.general.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c " +
            "WHERE c.author.id IN ?1 " +
            "AND c.event.id IN ?2 " +
            "AND c.state IN ?3 " +
            "AND c.createdOn BETWEEN ?4 AND ?5 " +
            "AND (LOWER(c.text) LIKE CONCAT('%', LOWER(?6), '%') OR ?6 IS NULL) " +
            "AND (c.isPositive = ?7 OR ?7 IS NULL)")
    Page<Comment> adminFindFilteredComments(List<Long> userIds,
                                            List<Long> eventIds,
                                            List<StateComment> stateComment,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            String text,
                                            Boolean isPositive,
                                            Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.state = 'PUBLISHED' " +
            "AND c.event.id = ?1")
    List<Comment> publicFindCommentsOfEvent(Long eventId);

    List<Comment> findAllByEvent_Id(Long eventId);

    List<Comment> findAllByAuthor_Id(Long authorId);

}