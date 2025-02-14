package com.example.mscreditservice.controller;

import com.example.mscreditservice.dto.CreditDTO;
import com.example.mscreditservice.model.Credit;
import com.example.mscreditservice.model.CreditRequest;
import com.example.mscreditservice.model.PaymentRequest;
import com.example.mscreditservice.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {
    private final CreditService creditService;

    @GetMapping("/customer/{customerId}")
    public Flux<Credit> getCreditsByCustomerId(@PathVariable String customerId) {
        return creditService.getCreditsByCustomerId(customerId);
    }
    @PostMapping("/personal")
    public Mono<Credit> createPersonalCredit(@RequestBody CreditRequest request) {
        return creditService.createPersonalCredit(request);
    }

    @PostMapping("/business")
    public Mono<Credit> createBusinessCredit(@RequestBody CreditRequest request) {
        return creditService.createBusinessCredit(request);
    }
    @PostMapping("/{creditId}/pay")
    public Mono<Credit> payCredit(@PathVariable String creditId, @RequestBody PaymentRequest paymentRequest) {
        return creditService.payCredit(creditId, paymentRequest.getPaymentAmount());
    }

}


