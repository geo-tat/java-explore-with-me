package ru.yandex.practicum.comment.service;

import ru.yandex.practicum.comment.dto.CommentFullDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    List<CommentFullDto> getCommentsForEvent(Long eventId);

    CommentFullDto getCommentById(Long commentId);

    void deleteCommentByAdmin(Long commentId);

    CommentFullDto addComment(Long userId, Long eventId, NewCommentDto dto);

    CommentFullDto updateComment(Long userId, Long commentId, NewCommentDto dto);

    void delete(Long userId, Long commentId);

    List<CommentFullDto> getAllCommentsByUser(Long userId);
}
