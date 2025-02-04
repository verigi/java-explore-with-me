package ru.practicum.general.exceptions;

public class CustomAccessException extends RuntimeException {
    public CustomAccessException(final String msg) {
        super(msg);
    }
}