package ru.practicum.privateApi.service.comment;

import ru.practicum.general.dto.comment.CommentDto;
import ru.practicum.general.dto.comment.CreateCommentDto;
import ru.practicum.general.dto.comment.update.UpdateCommentUserRequest;

import java.util.List;

public interface PrivateCommentService {
    CommentDto createComment(Long userId, Long eventId, CreateCommentDto createCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentUserRequest request);

    void deleteComment(Long commentId, Long userId);

    List<CommentDto> getAllCommentsFromCurrentUser(Long userId);
}