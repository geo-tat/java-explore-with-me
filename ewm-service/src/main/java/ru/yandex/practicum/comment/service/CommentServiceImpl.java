package ru.yandex.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.comment.dto.CommentMapper;
import ru.yandex.practicum.comment.repository.CommentRepository;
import ru.yandex.practicum.comment.dto.CommentFullDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentFullDto addComment(Long userId, Long eventId, NewCommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + eventId + " not found!"));
        Comment comment = CommentMapper.toEntity(dto, user, event);

        return CommentMapper.toDto(repository.save(comment));
    }

    @Override
    public List<CommentFullDto> getCommentsForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + eventId + " not found!"));
        List<Comment> comments = repository.findAllByEventId(eventId);

        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentFullDto getCommentById(Long commentId) {
        Comment comment = repository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment id=" + commentId + " not found"));
        return CommentMapper.toDto(comment);
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment id=" + commentId + " not found"));
        repository.delete(comment);
    }


    @Override
    public CommentFullDto updateComment(Long userId, Long commentId, NewCommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment id=" + commentId + " not found"));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ValidationException("You don't have permission to edit this comment");
        }
        if (dto.getText() != null && !dto.getText().isBlank()) {
            comment.setText(dto.getText());
        }
        return CommentMapper.toDto(repository.save(comment));
    }

    @Override
    public void delete(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment id=" + commentId + " not found"));
        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new ValidationException("You don't have permission to edit this comment");
        }
        repository.delete(comment);
    }

    @Override
    public List<CommentFullDto> getAllCommentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));

        List<Comment> comments = repository.findAllByAuthorId(userId);
        return comments
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }
}
