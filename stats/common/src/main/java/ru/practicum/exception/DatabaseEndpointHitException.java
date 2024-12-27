package ru.practicum.exception;

public class DatabaseEndpointHitException extends RuntimeException {
    public DatabaseEndpointHitException(final String msg) {
        super(msg);
    }
}