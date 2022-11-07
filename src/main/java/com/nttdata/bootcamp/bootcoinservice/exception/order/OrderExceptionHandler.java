package com.nttdata.bootcamp.bootcoinservice.exception.order;

import com.nttdata.bootcamp.bootcoinservice.util.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MessageResponse handleOrderNotFoundException(OrderNotFoundException ex) {
        return MessageResponse.builder().message(ex.getMessage()).build();
    }

    @ExceptionHandler(OrderCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageResponse handleOrderCreationException(OrderCreationException ex) {
        return MessageResponse.builder().message(ex.getMessage()).build();
    }
}
