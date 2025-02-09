package ru.practicum.adminApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.adminApi.service.comment.AdminCommentService;
import ru.practicum.general.dto.comment.CommentDto;
import ru.practicum.general.dto.comment.update.UpdateCommentAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {
    private final AdminCommentService service;

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        log.debug("New DELETE request received. Delete comment. Id={}", commentId);
        service.deleteComment(commentId);
        log.debug("Comment with id {} successfully deleted", commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("commentId") Long commentId,
                                                    @Valid @RequestBody UpdateCommentAdminRequest request) {
        log.debug("New PATCH request received. Update comment. Id={}", commentId);
        CommentDto commentDto = service.updateComment(commentId, request);
        log.debug("Comment with id {} successfully updated", commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentDto);
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<CommentDto>> getAllCommentsOfEvent(@PathVariable("eventId") Long eventId) {
        log.debug("New GET request received. Get all comments of event. Event id={}", eventId);
        List<CommentDto> commentDtos = service.getAllCommentsOfEvent(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(commentDtos);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<CommentDto>> getAllCommentsFromUser(@PathVariable("userId") Long userId) {
        log.debug("New GET request received. Get all comments from user. User id={}", userId);
        List<CommentDto> commentDtos = service.getAllCommentsFromUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(commentDtos);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CommentDto>> getFilteredComments(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "false") Boolean isPositive,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("New GET request received. Get filtered comments. Parameters: users={}, events={}, states={}, start={}, end={}, text={}, positive={}, from={}, size={}",
                users,
                events,
                states,
                rangeStart,
                rangeEnd,
                text,
                isPositive,
                from,
                size);
        List<CommentDto> commentDtos = service.getFilteredComments(users, events, states, rangeStart, rangeEnd, text, isPositive, from, size);
        return ResponseEntity.status(HttpStatus.OK).body(commentDtos);
    }
}