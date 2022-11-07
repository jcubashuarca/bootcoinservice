package com.nttdata.bootcamp.bootcoinservice.service;

import com.nttdata.bootcamp.bootcoinservice.dto.PaymentMethodRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.WalletRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.WalletResponse;
import com.nttdata.bootcamp.bootcoinservice.util.MessageResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletService {

    Flux<WalletResponse> getAll();
    Mono<WalletResponse> save(WalletRequest walletRequest);

    Mono<MessageResponse> checkPaymentMethod(PaymentMethodRequest paymentMethodRequest);
}
