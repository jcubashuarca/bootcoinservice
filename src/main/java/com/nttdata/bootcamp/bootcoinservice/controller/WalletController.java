package com.nttdata.bootcamp.bootcoinservice.controller;

import com.nttdata.bootcamp.bootcoinservice.dto.PaymentMethodRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.WalletRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.WalletResponse;
import com.nttdata.bootcamp.bootcoinservice.service.WalletService;
import com.nttdata.bootcamp.bootcoinservice.util.MessageResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/wallets")
@AllArgsConstructor
@Slf4j
public class WalletController {
    @Autowired
    private WalletService walletService;

    //Ver todos los monedereos del cliente
    @GetMapping
    public Flux<WalletResponse> getAll() {
        return walletService.getAll();
    }

    //Permite la creacion de monederos virtuales
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<WalletResponse> save(@Valid @RequestBody WalletRequest walletRequest) {
        return walletService.save(walletRequest);
    }

    //Validar Metodo de pago del vendedor
    //Un usuario vendedor tiene que poseer un monedero móvil Yanki o tener una
    //cuenta bancaria de ahorro o corriente en el banco.
    @PostMapping("check-payment-method")
    public Mono<MessageResponse> checkPaymentMethod(@Valid @RequestBody PaymentMethodRequest paymentMethodRequest) {
        return walletService.checkPaymentMethod(paymentMethodRequest);
    }

}
