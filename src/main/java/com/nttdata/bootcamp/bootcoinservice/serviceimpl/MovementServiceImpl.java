package com.nttdata.bootcamp.bootcoinservice.serviceimpl;

import com.nttdata.bootcamp.bootcoinservice.dto.MovementRequest;
import com.nttdata.bootcamp.bootcoinservice.dto.MovementResponse;
import com.nttdata.bootcamp.bootcoinservice.exception.movement.MovementCreationException;
import com.nttdata.bootcamp.bootcoinservice.repository.MovementRepository;
import com.nttdata.bootcamp.bootcoinservice.repository.WalletRepository;
import com.nttdata.bootcamp.bootcoinservice.service.MovementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class MovementServiceImpl implements MovementService {

    @Autowired
    private MovementRepository movementRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private StreamBridge streamBridge;

    @Override
    public Flux<MovementResponse> getAllByWallet(String wallet) {
        return movementRepository.findByWallet(wallet).map(MovementResponse::fromModel);
    }

    @Override
    public Mono<MovementResponse> save(MovementRequest movementRequest) {
        return Mono.just(movementRequest)
                .map(MovementRequest::toModel)
                .flatMap(movement -> walletRepository.findById(movementRequest.getWallet())
                        .switchIfEmpty(Mono.error(new MovementCreationException("Wallet not found with phone: "+movementRequest.getWallet())))
                        .flatMap(existingWallet -> {
                            if(existingWallet.validMovement(movement.getType(), movement.getAmount()))
                                return Mono.error(new MovementCreationException("You reach the limit of your wallet"));

                            existingWallet.makeMovement(movementRequest.getType(), movementRequest.getAmount());
                            movement.setWallet(existingWallet.getId());

                            return walletRepository.save(existingWallet)
                                    .then(movementRepository.insert(movement));
                        }))
                .map(MovementResponse::fromModel)
                .onErrorMap(ex -> new MovementCreationException(ex.getMessage()))
                .doOnError(ex -> log.error("Error save movement", ex));
    }
}
