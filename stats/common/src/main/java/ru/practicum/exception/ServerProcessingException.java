package ru.practicum.exception;

public class ServerProcessingException extends RuntimeException {
    public ServerProcessingException(final String msg) {
        super(msg);
    }
}