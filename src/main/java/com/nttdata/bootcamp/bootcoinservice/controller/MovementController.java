package com.nttdata.bootcamp.bootcoinservice.controller;

import com.nttdata.bootcamp.bootcoinservice.dto.MovementRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.MovementResponse;
import com.nttdata.bootcamp.bootcoinservice.service.MovementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/movements")
@AllArgsConstructor
@Slf4j
public class MovementController {
    @Autowired
    private MovementService movementService;

    @GetMapping("{wallet}")
    public Flux<MovementResponse> getAllByWallet(@PathVariable String wallet) {
        return movementService.getAllByWallet(wallet);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovementResponse> save(@Valid @RequestBody MovementRequest movementRequest) {
        return movementService.save(movementRequest);
    }
}
