package ru.practicum.exception;

public class ClientRequestException extends RuntimeException {
    public ClientRequestException(final String msg) {
        super(msg);
    }
}