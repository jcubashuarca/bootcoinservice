package com.nttdata.bootcamp.bootcoinservice.serviceimpl;

import com.nttdata.bootcamp.bootcoinservice.dto.PaymentMethodRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.WalletRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.WalletResponse;
import com.nttdata.bootcamp.bootcoinservice.exception.wallet.WalletCreationException;
import com.nttdata.bootcamp.bootcoinservice.exception.wallet.WalletNotFoundException;
import com.nttdata.bootcamp.bootcoinservice.repository.WalletRepository;
import com.nttdata.bootcamp.bootcoinservice.service.WalletService;
import com.nttdata.bootcamp.bootcoinservice.util.MessageResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private MessageService messageService;

    @Override
    public Flux<WalletResponse> getAll() {
        return walletRepository.findAll().map(WalletResponse::fromModel);
    }

    @Override
    public Mono<WalletResponse> save(WalletRequest walletRequest) {
        return Mono.just(walletRequest)
                .flatMap(walletRequest1 -> walletRepository.existsByPhone(walletRequest1.getPhone())
                        .flatMap(existing -> existing ?
                                Mono.error(new WalletCreationException("Phone number exists "+walletRequest1.getPhone())):
                                Mono.just(walletRequest1))
                        )
                .map(WalletRequest::toModel)
                .flatMap(wallet -> walletRepository.save(wallet))
                .map(WalletResponse::fromModel)
                .onErrorMap(ex -> new WalletCreationException(ex.getMessage()))
                .doOnError(ex -> log.error("Error creating new Account ", ex));
    }

    @Override
    public Mono<MessageResponse> checkPaymentMethod(PaymentMethodRequest paymentMethodRequest) {
        return Mono.just(paymentMethodRequest)
                .map(PaymentMethodRequest::toMessageKafka)
                .flatMap(messageKafka -> walletRepository.findById(paymentMethodRequest.getWallet())
                        .switchIfEmpty(Mono.error(new WalletNotFoundException("Wallet not found with id: "+paymentMethodRequest.getWallet())))
                        .flatMap(existing -> {
                            if(paymentMethodRequest.getAccountType().equalsIgnoreCase("yanki")) {
                                messageKafka.setNumber(existing.getPhone());
                            }
                            else {
                                if(Objects.isNull(paymentMethodRequest.getAccountNumber())) {
                                    return Mono.error(new WalletCreationException("field accountNumber is required"));
                                }
                            }

                            existing.setAccountType(paymentMethodRequest.getAccountType());
                            existing.setAccountNumber(paymentMethodRequest.getAccountNumber());
                            existing.setCanSell(false);

                            return walletRepository.save(existing)
                                    .then(Mono.just(new MessageResponse("Registered Successfully")));
                        }).doOnSuccess(res -> {
                            if (paymentMethodRequest.getAccountType().equalsIgnoreCase("yanki")) {
                                messageService.linkAccountToYanki(messageKafka);
                            }
                            else {
                                messageService.linkAccountToAccount(messageKafka);
                            }
                        })
                )
                .onErrorMap(ex -> new WalletCreationException(ex.getMessage()))
                .doOnError(ex -> log.error("Error creating new Account ", ex));
    }
}
