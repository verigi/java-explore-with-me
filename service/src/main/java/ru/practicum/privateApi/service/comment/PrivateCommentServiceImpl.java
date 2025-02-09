package ru.practicum.privateApi.service.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.general.dto.comment.CommentDto;
import ru.practicum.general.dto.comment.CreateCommentDto;
import ru.practicum.general.dto.comment.update.UpdateCommentUserRequest;
import ru.practicum.general.exceptions.CustomAccessException;
import ru.practicum.general.mapper.CommentMapper;
import ru.practicum.general.model.Comment;
import ru.practicum.general.model.Event;
import ru.practicum.general.model.User;
import ru.practicum.general.repository.CommentRepository;
import ru.practicum.general.repository.EventRepository;
import ru.practicum.general.repository.UserRepository;
import ru.practicum.general.util.EntityHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PrivateCommentServiceImpl implements PrivateCommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final EntityHandler entityHandler;


    @Autowired
    public PrivateCommentServiceImpl(CommentRepository commentRepository,
                                     UserRepository userRepository,
                                     EventRepository eventRepository,
                                     CommentMapper commentMapper,
                                     EntityHandler entityHandler) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.commentMapper = commentMapper;
        this.entityHandler = entityHandler;
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long eventId, CreateCommentDto createCommentDto) {
        log.debug("Attempting to create comment from user={}, to event={}, text={}",
                userId,
                eventId,
                createCommentDto.getText());

        User user = entityHandler.findEntityById(userRepository, userId, "User");
        Event event = entityHandler.findEntityById(eventRepository, eventId, "Event");
        entityHandler.validateUserEventStateToComment(event);

        Comment comment = commentMapper.toEntity(createCommentDto);
        comment.setAuthor(user);
        comment.setEvent(event);
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentUserRequest request) {
        log.debug("Attempting to update comment. User id={}, comment id={}, new text={}",
                userId,
                commentId,
                request.getText());

        Comment comment = entityHandler.findEntityById(commentRepository, commentId, "Comment");
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new CustomAccessException("User can only update his comment");
        }
        comment = commentMapper.updateEntity(comment, request);
        commentRepository.flush();

        log.debug("Comment updated. Id={}", commentId);
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        log.debug("Attempting to delete comment. Id={}", commentId);

        Comment comment = entityHandler.findEntityById(commentRepository, commentId, "Comment");
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new CustomAccessException("User can only delete his comment");
        }

        log.debug("Comment deleted. Id={}", commentId);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsFromCurrentUser(Long userId) {
        log.debug("Attempting to get all comments from current user. User id={}", userId);
        List<Comment> comments = commentRepository.findAllByAuthor_Id(userId);
        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> commentMapper.toDto(comment))
                .collect(Collectors.toList());

        log.debug("Comments fetch. Size={}", commentDtos.size());
        return commentDtos;
    }
}