package ru.yandex.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidParameterException(final ConstraintViolationException e) {
        log.error(e.getMessage(), e);
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<String> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
            } else {
                errors.add(error.getDefaultMessage());
            }
        }
        return errors;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(EntityNotFoundException e) {
        ;
        log.error(e.getMessage(), e);
        List<String> exception = new ArrayList<>();
        for (StackTraceElement element : e.getStackTrace())
            exception.add(element.toString());
        return ApiError.builder()
                .errors(exception)
                .message(e.getMessage())
                .reason(HttpStatus.NOT_FOUND.getReasonPhrase())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ValidateDateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)    //409
    public ApiError handleDateValidationException(ValidateDateException e) {
        log.error("Time validation error");
        List<String> exception = new ArrayList<>();
        for (StackTraceElement element : e.getStackTrace())
            exception.add(element.toString());
        return ApiError.builder()
                .errors(exception)
                .message(e.getMessage())
                .reason(HttpStatus.CONFLICT.getReasonPhrase())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleUpdateValidationException(ValidationUpdateException e) {
        log.error("Status for update error", e);
        List<String> exception = new ArrayList<>();
        for (StackTraceElement element : e.getStackTrace())
            exception.add(element.toString());
        return ApiError.builder()
                .errors(exception)
                .message(e.getMessage())
                .reason(HttpStatus.CONFLICT.getReasonPhrase())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidationException(ValidationException e) {
        log.error("Validation error", e);
        List<String> exception = new ArrayList<>();
        for (StackTraceElement element : e.getStackTrace())
            exception.add(element.toString());
        return ApiError.builder()
                .errors(exception)
                .message(e.getMessage())
                .reason(HttpStatus.CONFLICT.getReasonPhrase())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        List<String> exception = new ArrayList<>();
        for (StackTraceElement element : e.getStackTrace())
            exception.add(element.toString());
        return ApiError.builder()
                .errors(exception)
                .message(e.getMessage())
                .reason(HttpStatus.CONFLICT.getReasonPhrase())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ValidateDateRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)    //409
    public ApiError handleDateValidateRangeException(ValidateDateRangeException e) {
        log.error("Time validation error");
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
