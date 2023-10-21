package ru.yandex.practicum.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.comment.dto.CommentFullDto;
import ru.yandex.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/events")
public class CommentPublicController {

    private final CommentService service;

    @GetMapping("/{eventId}/comments")
    List<CommentFullDto> getCommentsForEvent(@PathVariable Long eventId) {
        return service.getCommentsForEvent(eventId);
    }

    @GetMapping("comments/{commentId}")
    CommentFullDto getCommentById(@PathVariable Long commentId) {
        return service.getCommentById(commentId);
    }
}
