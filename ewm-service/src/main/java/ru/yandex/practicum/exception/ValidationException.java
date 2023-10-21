package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    String error;

    public ValidationException(String error) {
        this.error = error;
    }
}
