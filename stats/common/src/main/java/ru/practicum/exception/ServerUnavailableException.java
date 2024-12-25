package ru.practicum.exception;

public class ServerUnavailableException extends RuntimeException {
    public ServerUnavailableException(final String msg) {
        super(msg);
    }
}