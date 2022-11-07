package com.nttdata.bootcamp.bootcoinservice.controller;

import com.nttdata.bootcamp.bootcoinservice.dto.OrderRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.OrderResponse;
import com.nttdata.bootcamp.bootcoinservice.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    public Flux<OrderResponse> getAll() {
        return orderService.getAll();
    }

    //Cualquier usuario puede solicitar la compra de BootCoin especificando el
    //monto y modo de pago (Yanki o transferencia
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderResponse> save(@Valid @RequestBody OrderRequest orderRequest) {
        return orderService.save(orderRequest);
    }

    //Acepta el intercambio se genera un número de transacción.
    @PutMapping("{id}/take")
    public Mono<OrderResponse> take(@PathVariable String id, @RequestBody String walletSeller) {
        return orderService.take(id, walletSeller);
    }


    //El sistema con el número de transacción valida los datos de la operación
    //(monto, modo de pago, número de cuenta o de celular) para proceder con el
    //pago.
    @PutMapping("{id}/done")
    public Mono<OrderResponse> done(@PathVariable String id, @RequestBody String transactionNumber) {
        return orderService.done(id, transactionNumber);
    }
}
