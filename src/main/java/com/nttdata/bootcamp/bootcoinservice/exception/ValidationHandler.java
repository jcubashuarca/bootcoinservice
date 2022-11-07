package com.nttdata.bootcamp.bootcoinservice.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice

public class ValidationHandler {
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<List<String >> handleException(WebExchangeBindException ex) {
        return ResponseEntity.badRequest().body(ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList()));
    }
}
