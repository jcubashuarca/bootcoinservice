package com.nttdata.bootcamp.bootcoinservice.serviceimpl;

import com.nttdata.bootcamp.bootcoinservice.dto.MessageKafka;
import com.nttdata.bootcamp.bootcoinservice.dto.OrderRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.OrderResponse;
import com.nttdata.bootcamp.bootcoinservice.exception.order.OrderCreationException;
import com.nttdata.bootcamp.bootcoinservice.exception.order.OrderNotFoundException;
import com.nttdata.bootcamp.bootcoinservice.repository.OrderRepository;
import com.nttdata.bootcamp.bootcoinservice.repository.WalletRepository;
import com.nttdata.bootcamp.bootcoinservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MessageService messageService;

    @Override
    public Flux<OrderResponse> getAll() {
        return orderRepository.findAll().map(OrderResponse::fromModel);
    }

    @Override
    public Mono<OrderResponse> save(OrderRequest orderRequest) {
        return Mono.just(orderRequest)
                .map(OrderRequest::toModel)
                .flatMap(order -> walletRepository.findById(orderRequest.getWallet())
                            .switchIfEmpty(Mono.error(new OrderNotFoundException("Wallet not found with id: " + orderRequest.getWallet())))
                            .flatMap(existing -> {
                                order.calculateAmountBootcoin(orderRequest.getAmount());
                                return orderRepository.save(order);
                            })
                )
                .map(OrderResponse::fromModel);
    }

    @Override
    public Mono<OrderResponse> take(String id, String walletSeller) {
        return orderRepository.findById(id)
                        .switchIfEmpty(Mono.error(new OrderNotFoundException("Wallet not found with id: " + id)))
                        .flatMap(order -> walletRepository.findById(walletSeller)
                                .switchIfEmpty(Mono.error(new OrderNotFoundException("Wallet seller not found with id: " + walletSeller)))
                                .flatMap(wallet -> {
                                    if(!wallet.getCanSell()) {
                                        return Mono.error(new OrderCreationException("You must link your bank or yank account"));
                                    }
                                    if(wallet.getBalance() < order.getAmountBootcoin()) {
                                        return Mono.error(new OrderCreationException("You don't have enough balance"));
                                    }

                                    if(order.getPaymentMethod().equalsIgnoreCase("yanki")) {
                                        order.setDestinyNumber(wallet.getPhone());
                                    }
                                    else  {
                                        order.setDestinyNumber(wallet.getAccountNumber());
                                    }

                                    order.setWalletSeller(wallet.getId());
                                    order.setState("take");
                                    order.setUpdatedAt(LocalDateTime.now());
                                    return orderRepository.save(order);
                                }))
                .map(OrderResponse::fromModel);
    }

    @Override
    public Mono<OrderResponse> done(String id, String transactionNumber) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("Wallet not found with id: " + id)))
                .flatMap(order -> {
                    order.setState("done");
                    order.setTransactionNumber(transactionNumber);
                    order.setUpdatedAt(LocalDateTime.now());
                    return orderRepository.save(order).doOnSuccess(res -> {
                        MessageKafka messageKafka = MessageKafka.builder()
                                .type("request")
                                .document("movements")
                                .message(order.getTransactionNumber())
                                .amount(order.getAmount())
                                .build();
                        if (order.getPaymentMethod().equalsIgnoreCase("yanki")) {
                            messageService.linkAccountToYanki(messageKafka);

                        } else {
                            messageService.linkAccountToAccount(messageKafka);
                        }
                    });

                }).map(OrderResponse::fromModel);
    }
}
