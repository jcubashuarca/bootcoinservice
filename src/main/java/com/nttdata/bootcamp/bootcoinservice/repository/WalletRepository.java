package com.nttdata.bootcamp.bootcoinservice.repository;

import com.nttdata.bootcamp.bootcoinservice.model.Wallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface WalletRepository extends ReactiveMongoRepository<Wallet, String> {
    Mono<Wallet> findByPhone(String phone);
    Mono<Boolean> existsByPhone(String phone);

    Mono<Wallet> findByAccountNumber(String accountNumber);
}
