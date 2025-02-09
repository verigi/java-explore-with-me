package ru.practicum.publicApi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.general.dto.comment.CommentDto;
import ru.practicum.general.dto.comment.CommentShortDto;
import ru.practicum.publicApi.service.comment.PublicCommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class PublicCommentController {
    private final PublicCommentService service;

    @GetMapping(path = "/{commentId}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long commentId) {
        log.debug("New GET request received. Get full comment info. Comment id={}", commentId);
        CommentDto commentDto = service.getComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(commentDto);
    }

    @GetMapping(path = "/events/{eventId}")
    public ResponseEntity<List<CommentShortDto>> getCommentsOfEvent(@PathVariable Long eventId) {
        log.debug("New GET request received. Get comments of event. Event id={}", eventId);
        List<CommentShortDto> commentShortDtos = service.getCommentsOfEvent(eventId);
        return ResponseEntity.status(HttpStatus.OK).body(commentShortDtos);
    }
}