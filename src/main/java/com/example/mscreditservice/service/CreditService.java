package com.example.mscreditservice.service;


import com.example.mscreditservice.dto.CreditDTO;
import com.example.mscreditservice.model.Credit;
import com.example.mscreditservice.model.CreditRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CreditService {
    Flux<Credit> getCreditsByCustomerId(String customerId);
    Mono<Credit> createPersonalCredit(CreditRequest request);
    Mono<Credit> createBusinessCredit(CreditRequest request);
    Mono<Credit> payCredit(String creditId, BigDecimal paymentAmount);
}