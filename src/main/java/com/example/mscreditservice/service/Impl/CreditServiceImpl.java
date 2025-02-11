package com.example.mscreditservice.service.Impl;

import com.example.mscreditservice.Exception.BusinessException;
import com.example.mscreditservice.dto.request.CreditRequestDTO;
import com.example.mscreditservice.dto.response.CreditResponseDTO;
import com.example.mscreditservice.model.Credit;
import com.example.mscreditservice.repository.CreditRepository;
import com.example.mscreditservice.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {
    private final CreditRepository creditRepository;
    private static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("0.15"); // 15%

    @Override
    public Mono<CreditResponseDTO> createCredit(CreditRequestDTO request) {
        return validateCreditRequest(request)
                .flatMap(validRequest -> {
                    Credit credit = new Credit();
                    credit.setCustomerId(request.getCustomerId());
                    credit.setCustomerType(request.getCustomerType());
                    credit.setAmount(request.getAmount());
                    credit.setRemainingAmount(request.getAmount());
                    credit.setTerm(request.getTerm());
                    credit.setInterestRate(DEFAULT_INTEREST_RATE);
                    credit.setCreatedAt(LocalDateTime.now());
                    credit.setStatus("PENDING");

                    return creditRepository.save(credit)
                            .map(this::mapToResponse);
                });
    }

    private Mono<CreditRequestDTO> validateCreditRequest(CreditRequestDTO request) {
        return creditRepository.countByCustomerIdAndCustomerType(
                request.getCustomerId(),
                request.getCustomerType()
        ).flatMap(count -> {
            if ("PERSONAL".equals(request.getCustomerType()) && count > 0) {
                return Mono.error(new BusinessException("Personal customers can only have one credit"));
            }
            return Mono.just(request);
        });
    }

    @Override
    public Mono<CreditResponseDTO> getCreditById(String id) {
        return creditRepository.findById(id)
                .map(this::mapToResponse);
    }

    @Override
    public Flux<CreditResponseDTO> getCreditsByCustomerId(String customerId) {
        return creditRepository.findByCustomerId(customerId)
                .map(this::mapToResponse);
    }

    @Override
    public Mono<CreditResponseDTO> makePayment(String creditId, BigDecimal amount) {
        return creditRepository.findById(creditId)
                .flatMap(credit -> {
                    if (amount.compareTo(credit.getRemainingAmount()) > 0) {
                        return Mono.error(new BusinessException("Payment amount exceeds remaining balance"));
                    }

                    credit.setRemainingAmount(credit.getRemainingAmount().subtract(amount));
                    return creditRepository.save(credit);
                })
                .map(this::mapToResponse);
    }

    private CreditResponseDTO mapToResponse(Credit credit) {
        return CreditResponseDTO.builder()
                .id(credit.getId())
                .customerId(credit.getCustomerId())
                .customerType(credit.getCustomerType())
                .amount(credit.getAmount())
                .remainingAmount(credit.getRemainingAmount())
                .term(credit.getTerm())
                .interestRate(credit.getInterestRate())
                .createdAt(credit.getCreatedAt())
                .status(credit.getStatus())
                .build();
    }
}

