package com.nttdata.bootcamp.bootcoinservice.exception.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OrderCreationException extends RuntimeException{
    public OrderCreationException(String message) {super(message);}
}
