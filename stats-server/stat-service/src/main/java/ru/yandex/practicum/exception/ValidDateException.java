package ru.yandex.practicum.exception;

import lombok.Getter;

@Getter
public class ValidDateException extends RuntimeException {
    String error;

    public ValidDateException(String error) {
        this.error = error;
    }
}
