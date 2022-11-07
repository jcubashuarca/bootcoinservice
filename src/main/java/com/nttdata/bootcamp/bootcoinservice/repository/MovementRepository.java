package com.nttdata.bootcamp.bootcoinservice.repository;

import com.nttdata.bootcamp.bootcoinservice.model.Movement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MovementRepository extends ReactiveMongoRepository<Movement, String> {
    Flux<Movement> findByWallet(String wallet);
}
