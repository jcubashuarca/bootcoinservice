package com.nttdata.bootcamp.bootcoinservice.exception.movement;

import com.nttdata.bootcamp.bootcoinservice.util.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class MovementExceptionHandler {
    @ExceptionHandler(MovementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MessageResponse handleMovementNotFoundException(MovementNotFoundException ex) {
        return MessageResponse.builder().message(ex.getMessage()).build();
    }

    @ExceptionHandler(MovementCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageResponse handleMovementCreationException(MovementCreationException ex) {
        return MessageResponse.builder().message(ex.getMessage()).build();
    }
}
