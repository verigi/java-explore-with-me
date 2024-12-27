package ru.practicum.exception;

public class InvalidEndpointHitException extends RuntimeException {
    public InvalidEndpointHitException(final String msg) {
        super(msg);
    }
}