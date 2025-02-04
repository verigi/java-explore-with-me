package ru.practicum.general.exceptions;

public class InvalidStateException extends RuntimeException {
    public InvalidStateException(final String msg) {
        super(msg);
    }
}