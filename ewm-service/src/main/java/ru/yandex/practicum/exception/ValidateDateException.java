package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class ValidateDateException extends RuntimeException {
    String error;

    public ValidateDateException(String error) {
        this.error = error;
    }
}
