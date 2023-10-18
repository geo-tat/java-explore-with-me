package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class ValidationUpdateException extends RuntimeException {
    String error;

    public ValidationUpdateException(String error) {
        this.error = error;
    }
}
