package com.nttdata.bootcamp.bootcoinservice.service;

import com.nttdata.bootcamp.bootcoinservice.dto.OrderRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.OrderResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    Flux<OrderResponse> getAll();
    Mono<OrderResponse> save(OrderRequest orderRequest);

    Mono<OrderResponse> take(String id, String walletSeller);

    Mono<OrderResponse> done(String id, String transactionNumber);
}

