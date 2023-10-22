package ru.yandex.practicum.comment.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.dto.CommentFullDto;
import ru.yandex.practicum.comment.service.CommentService;
import ru.yandex.practicum.comment.dto.NewCommentDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/users/{userId}")
public class CommentPrivateController {

    private final CommentService service;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    CommentFullDto addComment(@PathVariable Long userId,
                              @PathVariable Long eventId,
                              @RequestBody @Valid NewCommentDto dto) {
        return service.addComment(userId,eventId,dto);
    }

    @PatchMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    CommentFullDto updateComment(@PathVariable Long userId,
                                 @PathVariable Long commentId,
                                 @RequestBody @Valid NewCommentDto dto) {
        return service.updateComment(userId,commentId,dto);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable Long userId,
                       @PathVariable Long commentId) {
        service.delete(userId,commentId);
    }

   @GetMapping("/comments")
   @ResponseStatus(HttpStatus.OK)
   List<CommentFullDto> getCommentsByUser(@PathVariable Long userId) {
        return service.getAllCommentsByUser(userId);
   }

}
