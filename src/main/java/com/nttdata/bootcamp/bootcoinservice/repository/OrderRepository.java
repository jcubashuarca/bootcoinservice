package com.nttdata.bootcamp.bootcoinservice.repository;

import com.nttdata.bootcamp.bootcoinservice.model.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
    Mono<Order> findByWallet(String wallet);

    Mono<Order> findByTransactionNumber(String transactionNumber);
}
