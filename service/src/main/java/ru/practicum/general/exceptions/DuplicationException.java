package ru.practicum.general.exceptions;

public class DuplicationException extends RuntimeException {
    public DuplicationException(final String msg) {
        super(msg);
    }
}