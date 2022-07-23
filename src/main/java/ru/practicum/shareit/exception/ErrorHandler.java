package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn(e.getMessage());
        log.info(e.getMessage());
        return new ErrorResponse(
                e.getMessage()
        );
    }

    @ExceptionHandler(value = {DublEmail.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDublEmailException(final DublEmail e) {
        log.info(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.info(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

}
