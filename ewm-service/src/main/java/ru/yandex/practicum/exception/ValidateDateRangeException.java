package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class ValidateDateRangeException extends RuntimeException {
    String error;

    public ValidateDateRangeException(String error) {
        this.error = error;
    }
}
