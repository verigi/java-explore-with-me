package ru.practicum.general.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.general.dto.comment.CommentDto;
import ru.practicum.general.dto.comment.CommentShortDto;
import ru.practicum.general.dto.comment.CreateCommentDto;
import ru.practicum.general.dto.comment.update.UpdateComment;
import ru.practicum.general.dto.comment.update.UpdateCommentAdminRequest;
import ru.practicum.general.dto.comment.update.UpdateCommentUserRequest;
import ru.practicum.general.enums.StateComment;
import ru.practicum.general.model.Comment;

@Component
public class CommentMapper {
    private final UserMapper userMapper;
    private final EventMapper eventMapper;


    @Autowired
    public CommentMapper(UserMapper userMapper, EventMapper eventMapper) {
        this.userMapper = userMapper;
        this.eventMapper = eventMapper;
    }

    public CommentDto toDto(Comment comment) {
        return comment == null ? null : CommentDto.builder()
                .id(comment.getId())
                .author(userMapper.toShortDto(comment.getAuthor()))
                .event(eventMapper.toShortDto(comment.getEvent()))
                .createdOn(comment.getCreatedOn())
                .updatedOn(comment.getUpdatedOn())
                .text(comment.getText())
                .isPositive(comment.getIsPositive())
                .state(comment.getState())
                .build();
    }

    public CommentShortDto toShortDto(Comment comment) {
        return comment == null ? null : CommentShortDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .isPositive(comment.getIsPositive())
                .author(userMapper.toShortDto(comment.getAuthor()))
                .createdOn(comment.getCreatedOn())
                .build();
    }

    public Comment toEntity(CreateCommentDto createCommentDto) {
        return createCommentDto == null ? null : Comment.builder()
                .text(createCommentDto.getText())
                .isPositive(createCommentDto.getIsPositive())
                .build();
    }

    public Comment updateEntity(Comment comment, UpdateComment updateComment) {
        if (updateComment instanceof UpdateCommentAdminRequest request) {
            applyAdminChanges(comment, request);
        } else if (updateComment instanceof UpdateCommentUserRequest request) {
            applyUserChanges(comment, request);
        } else {
            throw new IllegalArgumentException("Unsupported DTO type: " + updateComment.getClass().getSimpleName());
        }
        return comment;

    }

    private void applyAdminChanges(Comment comment, UpdateCommentAdminRequest request) {
        switch (request.getStateAction()) {
            case PUBLISH_COMMENT -> comment.setState(StateComment.PUBLISHED);
            case REJECT_COMMENT -> comment.setState(StateComment.REJECTED);
            default ->
                    throw new IllegalArgumentException("Incorrect action state for admin: " + request.getStateAction());
        }
    }

    private void applyUserChanges(Comment comment, UpdateCommentUserRequest request) {
        if (request.getText() != null) {
            comment.setText(request.getText());
            comment.setState(StateComment.PENDING);
        }
    }


}