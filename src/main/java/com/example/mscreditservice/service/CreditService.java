package com.example.mscreditservice.service;

import com.example.mscreditservice.dto.request.CreditRequestDTO;
import com.example.mscreditservice.dto.response.CreditResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CreditService {
    Mono<CreditResponseDTO> createCredit(CreditRequestDTO request);
    Mono<CreditResponseDTO> getCreditById(String id);
    Flux<CreditResponseDTO> getCreditsByCustomerId(String customerId);
    Mono<CreditResponseDTO> makePayment(String creditId, BigDecimal amount);
}