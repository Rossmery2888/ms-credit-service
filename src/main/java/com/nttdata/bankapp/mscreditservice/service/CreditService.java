package com.nttdata.bankapp.mscreditservice.service;

import com.nttdata.bankapp.mscreditservice.dto.CreditBalanceDto;
import com.nttdata.bankapp.mscreditservice.dto.CreditDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Interfaz que define los servicios para operaciones con créditos.
 */
public interface CreditService {
    Flux<CreditDto> findAll();
    Mono<CreditDto> findById(String id);
    Flux<CreditDto> findByCustomerId(String customerId);
    Mono<CreditDto> findByCreditNumber(String creditNumber);
    Mono<CreditDto> save(CreditDto creditDto);
    Mono<CreditDto> update(String id, CreditDto creditDto);
    Mono<Void> delete(String id);
    Mono<CreditBalanceDto> getBalance(String id);
    Mono<CreditDto> makePayment(String id, BigDecimal amount);
}
