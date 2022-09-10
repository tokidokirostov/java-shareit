package ru.practicum.shareit.exception;

public class DublEmail extends RuntimeException {
    public DublEmail(String message) {
        super(message);
    }
}
