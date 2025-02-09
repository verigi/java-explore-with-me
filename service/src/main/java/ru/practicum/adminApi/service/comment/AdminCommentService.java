package ru.practicum.adminApi.service.comment;


import ru.practicum.general.dto.comment.CommentDto;
import ru.practicum.general.dto.comment.update.UpdateCommentAdminRequest;


import java.time.LocalDateTime;
import java.util.List;

public interface AdminCommentService {
    void deleteComment(Long commentId);

    CommentDto updateComment(Long commentId,
                             UpdateCommentAdminRequest updateCommentAdminRequest);

    List<CommentDto> getAllCommentsOfEvent(Long eventId);

    List<CommentDto> getAllCommentsFromUser(Long userId);

    List<CommentDto> getFilteredComments(List<Long> userIds,
                                         List<Long> eventIds,
                                         List<String> stateComment,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         String text,
                                         Boolean isPositive,
                                         int from,
                                         int size);
}