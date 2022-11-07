package com.nttdata.bootcamp.bootcoinservice.service;

import com.nttdata.bootcamp.bootcoinservice.dto.MovementRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.MovementResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementService {
    Flux<MovementResponse> getAllByWallet(String wallet);
    Mono<MovementResponse> save(MovementRequest walletMovementRequest);
}
