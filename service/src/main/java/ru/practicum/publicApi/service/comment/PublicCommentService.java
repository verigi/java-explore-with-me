package ru.practicum.publicApi.service.comment;

import ru.practicum.general.dto.comment.CommentDto;
import ru.practicum.general.dto.comment.CommentShortDto;

import java.util.List;

public interface PublicCommentService {
    List<CommentShortDto> getCommentsOfEvent(Long eventId);

    CommentDto getComment(Long commentId);
}