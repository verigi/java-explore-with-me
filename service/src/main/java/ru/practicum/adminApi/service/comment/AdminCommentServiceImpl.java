package ru.practicum.adminApi.service.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.general.dto.comment.CommentDto;
import ru.practicum.general.dto.comment.update.UpdateCommentAdminRequest;
import ru.practicum.general.enums.StateComment;
import ru.practicum.general.mapper.CommentMapper;
import ru.practicum.general.model.Comment;
import ru.practicum.general.model.Event;
import ru.practicum.general.model.User;
import ru.practicum.general.repository.CommentRepository;
import ru.practicum.general.repository.EventRepository;
import ru.practicum.general.repository.UserRepository;
import ru.practicum.general.util.EntityHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdminCommentServiceImpl implements AdminCommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final EntityHandler entityHandler;

    @Autowired
    public AdminCommentServiceImpl(CommentRepository commentRepository,
                                   EventRepository eventRepository,
                                   UserRepository userRepository,
                                   CommentMapper commentMapper,
                                   EntityHandler entityHandler) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
        this.entityHandler = entityHandler;
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        log.debug("Attempting to delete comment. Id={}", commentId);

        Comment comment = entityHandler.findEntityById(commentRepository, commentId, "Comment");
        commentRepository.delete(comment);

        log.debug("Comment deleted. Id={}", comment.getId());
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long commentId, UpdateCommentAdminRequest updateCommentAdminRequest) {
        log.debug("Attempting to update comment. Id={}, action state={}",
                commentId,
                updateCommentAdminRequest.getStateAction());

        Comment comment = entityHandler.findEntityById(commentRepository, commentId, "Comment");
        comment = commentMapper.updateEntity(comment, updateCommentAdminRequest);
        commentRepository.flush();

        log.debug("Comment state updated. Id={}", commentId);
        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getFilteredComments(List<Long> userIds,
                                                List<Long> eventIds,
                                                List<String> stateComment,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                String text,
                                                Boolean isPositive,
                                                int from,
                                                int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (userIds == null || userIds.isEmpty()) {
            userIds = userRepository.findAll().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            log.debug("No user ids entered. Default value provided: {}", userIds);
        }
        if (eventIds == null || eventIds.isEmpty()) {
            eventIds = eventRepository.findAll().stream()
                    .map(Event::getId)
                    .collect(Collectors.toList());
            log.debug("No event ids entered. Default value provided: {}", eventIds);
        }
        if (stateComment == null || stateComment.isEmpty()) {
            stateComment = List.of(
                    StateComment.PENDING.toString(),
                    StateComment.PUBLISHED.toString(),
                    StateComment.REJECTED.toString());
            log.debug("No state entered. Default value provided: {}", stateComment);
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.of(1970, 1, 1, 0, 0);
            log.debug("No start time entered. Default value provided: {}", rangeStart);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(2100, 1, 1, 0, 0);
            log.debug("No end time entered. Default value provided: {}", rangeEnd);
        }
        log.debug("Attempting to get comments. User ids={}, event ids={}, state={}, start={}, end={}, text={}, positive={}. Pagination: from={}, size={}",
                userIds,
                eventIds,
                stateComment,
                rangeStart,
                rangeEnd,
                text,
                isPositive,
                from,
                size);
        List<StateComment> states = entityHandler.parseStates(stateComment, StateComment.class);

        Page<Comment> comments = commentRepository.adminFindFilteredComments(userIds, eventIds, states, rangeStart, rangeEnd, text, isPositive, pageable);

        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> commentMapper.toDto(comment))
                .collect(Collectors.toList());

        if (comments.isEmpty()) {
            log.debug("No comments found for the given filters");
        } else {
            log.debug("Comments fetched. Size={}", commentDtos.size());
        }
        return commentDtos;
    }

    @Override
    public List<CommentDto> getAllCommentsOfEvent(Long eventId) {
        log.debug("Attempting to get all comment of event. Event id={}", eventId);

        Event event = entityHandler.findEntityById(eventRepository, eventId, "Event");
        List<Comment> comments = commentRepository.findAllByEvent_Id(eventId);
        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> commentMapper.toDto(comment))
                .collect(Collectors.toList());

        if (commentDtos.isEmpty()) {
            log.debug("No comments found");
        } else {
            log.debug("Comments fetched. Size={}", commentDtos.size());
        }
        return commentDtos;
    }

    @Override
    public List<CommentDto> getAllCommentsFromUser(Long userId) {
        log.debug("Attempting to get all comments from user. User id={}", userId);

        User user = entityHandler.findEntityById(userRepository, userId, "User");
        List<Comment> comments = commentRepository.findAllByAuthor_Id(userId);
        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> commentMapper.toDto(comment))
                .collect(Collectors.toList());

        if (commentDtos.isEmpty()) {
            log.debug("No comments found");
        } else {
            log.debug("Comments fetched. Size={}", commentDtos.size());
        }
        return commentDtos;
    }
}