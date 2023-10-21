package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidDateException e) {
        log.error("Код ошибки: {}, {}", HttpStatus.BAD_REQUEST, e.getMessage());
        List<String> exception = new ArrayList<>();
        for (StackTraceElement element : e.getStackTrace())
            exception.add(element.toString());
        return ApiError.builder()
                .errors(exception)
                .message(e.getMessage())
                .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(MethodArgumentNotValidException e) {
        log.error("Код ошибки: {}, {}", HttpStatus.BAD_REQUEST, e.getMessage());
        List<String> exception = new ArrayList<>();
        for (StackTraceElement element : e.getStackTrace())
            exception.add(element.toString());
        return ApiError.builder()
                .errors(exception)
                .message(e.getMessage())
                .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }
}