package ru.practicum.privateApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.general.dto.comment.CommentDto;
import ru.practicum.general.dto.comment.CreateCommentDto;
import ru.practicum.general.dto.comment.update.UpdateCommentUserRequest;
import ru.practicum.privateApi.service.comment.PrivateCommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class PrivateCommentController {
    private final PrivateCommentService service;

    @PostMapping(path = "/users/{userId}/events/{eventId}/comments")
    public ResponseEntity<CommentDto> saveComment(@PathVariable Long userId,
                                                  @PathVariable Long eventId,
                                                  @Valid @RequestBody CreateCommentDto createCommentDto) {
        log.debug("New POST request received. Create comment. User id={}, event id={}", userId, eventId);
        CommentDto commentDto = service.createComment(userId, eventId, createCommentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }

    @PatchMapping(path = "/users/{userId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long userId,
                                                    @PathVariable Long commentId,
                                                    @Valid @RequestBody UpdateCommentUserRequest request) {
        log.debug("New PATCH request received. Update comment. Id={}", commentId);
        CommentDto commentDto = service.updateComment(userId, commentId, request);
        return ResponseEntity.status(HttpStatus.OK).body(commentDto);
    }

    @DeleteMapping(path = "/users/{userId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long userId,
                                              @PathVariable Long commentId) {
        log.debug("New DELETE request received. Delete comment. Id={}", commentId);
        service.deleteComment(commentId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "/users/{userId}/comments")
    public ResponseEntity<List<CommentDto>> getAllComments(@PathVariable("userId") Long userId) {
        log.debug("New GET request received. Get all comments from current user. User id={}", userId);
        List<CommentDto> commentDtos = service.getAllCommentsFromCurrentUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(commentDtos);
    }
}