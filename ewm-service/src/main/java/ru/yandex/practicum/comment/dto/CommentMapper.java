package ru.yandex.practicum.comment.dto;


import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.event.dto.EventMapper;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.user.dto.UserMapper;
import ru.yandex.practicum.user.model.User;

public class CommentMapper {

    public static Comment toEntity(NewCommentDto dto, User user, Event event) {
        return Comment.builder()
                .text(dto.getText())
                .author(user)
                .event(event)
                .build();
    }

    public static CommentFullDto toDto(Comment comment) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .event(EventMapper.toShortDto(comment.getEvent()))
                .created(comment.getCreated())
                .updated(comment.getUpdated())
                .build();
    }
}
