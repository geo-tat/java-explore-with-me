package ru.yandex.practicum.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.service.CommentService;

@AllArgsConstructor
@RequestMapping(path = "/admin/comments")
@RestController
public class CommentAdminController {
    private final CommentService service;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable Long commentId) {
        service.deleteCommentByAdmin(commentId);
    }

}
