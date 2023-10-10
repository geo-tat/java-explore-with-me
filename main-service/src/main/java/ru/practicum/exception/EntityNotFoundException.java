package ru.practicum.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    String error;

    public EntityNotFoundException(String error) {
        this.error = error;
    }
}
