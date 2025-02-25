package com.example.mscreditservice.controller;

import com.example.mscreditservice.model.Credit;
import com.example.mscreditservice.model.CreditRequest;
import com.example.mscreditservice.model.PaymentRequest;
import com.example.mscreditservice.model.ThirdPartyPaymentRequest;
import com.example.mscreditservice.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
public class CreditController {
    private final CreditService creditService;

    @GetMapping("/customer/{customerId}")
    public Flux<Credit> getCreditsByCustomerId(@PathVariable String customerId) {
        return creditService.getCreditsByCustomerId(customerId);
    }

    @GetMapping("/customer/{customerId}/has-overdue-debt")
    public Mono<ResponseEntity<Boolean>> checkOverdueDebt(@PathVariable String customerId) {
        return creditService.hasOverdueDebt(customerId)
                .map(hasOverdue -> ResponseEntity.ok(hasOverdue));
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

    @PostMapping("/{creditId}/pay-third-party")
    public Mono<ResponseEntity<Credit>> payThirdPartyCredit(
            @PathVariable String creditId,
            @RequestBody ThirdPartyPaymentRequest paymentRequest) {
        return creditService.payThirdPartyCredit(
                        creditId,
                        paymentRequest.getPayerCustomerId(),
                        paymentRequest.getPaymentAmount())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Actualizar estados vencidos diariamente a la medianoche
    @Scheduled(cron = "0 0 0 * * *")
    public void updateOverdueCredits() {
        creditService.updateOverdueStatus().subscribe();
    }
}

