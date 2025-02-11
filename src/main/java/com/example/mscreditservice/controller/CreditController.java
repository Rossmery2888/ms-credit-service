package com.example.mscreditservice.controller;

import com.example.mscreditservice.dto.request.CreditRequestDTO;
import com.example.mscreditservice.dto.response.CreditResponseDTO;
import com.example.mscreditservice.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/credits")
@RequiredArgsConstructor
public class CreditController {
    private final CreditService creditService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreditResponseDTO> createCredit(@RequestBody CreditRequestDTO request) {
        return creditService.createCredit(request);
    }

    @GetMapping("/{id}")
    public Mono<CreditResponseDTO> getCredit(@PathVariable String id) {
        return creditService.getCreditById(id);
    }

    @GetMapping("/customer/{customerId}")
    public Flux<CreditResponseDTO> getCreditsByCustomer(@PathVariable String customerId) {
        return creditService.getCreditsByCustomerId(customerId);
    }

    @PostMapping("/{id}/payment")
    public Mono<CreditResponseDTO> makePayment(
            @PathVariable String id,
            @RequestParam BigDecimal amount
    ) {
        return creditService.makePayment(id, amount);
    }
}

