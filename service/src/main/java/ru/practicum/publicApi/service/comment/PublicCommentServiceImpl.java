package ru.practicum.publicApi.service.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.general.dto.comment.CommentDto;
import ru.practicum.general.dto.comment.CommentShortDto;
import ru.practicum.general.mapper.CommentMapper;
import ru.practicum.general.model.Comment;
import ru.practicum.general.model.Event;
import ru.practicum.general.repository.CommentRepository;
import ru.practicum.general.repository.EventRepository;
import ru.practicum.general.util.EntityHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PublicCommentServiceImpl implements PublicCommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final EntityHandler entityHandler;

    @Autowired
    public PublicCommentServiceImpl(CommentRepository commentRepository,
                                    EventRepository eventRepository,
                                    CommentMapper commentMapper,
                                    EntityHandler entityHandler) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.commentMapper = commentMapper;
        this.entityHandler = entityHandler;
    }

    @Override
    public List<CommentShortDto> getCommentsOfEvent(Long eventId) {
        Event event = entityHandler.findEntityById(eventRepository, eventId, "Event");
        entityHandler.validateUserEventStateToComment(event);
        log.debug("Attempting to get comments of event. Event id={}", eventId);
        List<Comment> comments = commentRepository.publicFindCommentsOfEvent(eventId);
        List<CommentShortDto> commentShortDtos = comments.stream()
                .map(comment -> commentMapper.toShortDto(comment))
                .collect(Collectors.toList());

        log.debug("Comments fetched. Size={}", commentShortDtos.size());
        return commentShortDtos;
    }

    public CommentDto getComment(Long commentId) {
        log.debug("Attempting to get comment of event. Comment id={}", commentId);
        Comment comment = entityHandler.findEntityById(commentRepository, commentId, "Comment");
        entityHandler.validateUserCommentStateToGet(comment);
        CommentDto commentDto = commentMapper.toDto(comment);

        log.debug("Comment fetched. Id={}", commentId);
        return commentDto;
    }
}