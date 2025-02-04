package ru.practicum.general.exceptions;

public class CustomConflictException extends RuntimeException {
    public CustomConflictException(final String msg) {
        super(msg);
    }
}