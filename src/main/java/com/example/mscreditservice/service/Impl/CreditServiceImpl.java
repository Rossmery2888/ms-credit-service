package com.example.mscreditservice.service.Impl;

import com.example.mscreditservice.Exception.BusinessException;
import com.example.mscreditservice.model.Credit;
import com.example.mscreditservice.model.CreditRequest;
import com.example.mscreditservice.repository.CreditRepository;
import com.example.mscreditservice.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {
    private final CreditRepository creditRepository;
    private static final int MAX_PERSONAL_CREDITS = 1;

    @Override
    public Flux<Credit> getCreditsByCustomerId(String customerId) {
        return creditRepository.findByCustomerId(customerId);
    }

    @Override
    public Mono<Credit> createPersonalCredit(CreditRequest request) {
        // Primero verificar si tiene deuda vencida
        return hasOverdueDebt(request.getCustomerId())
                .flatMap(hasOverdue -> {
                    if (hasOverdue) {
                        return Mono.error(new BusinessException("El cliente tiene deudas vencidas. No puede adquirir nuevos créditos."));
                    }

                    // Verifica si el cliente personal ya tiene un crédito activo
                    return creditRepository.findByCustomerIdAndCreditTypeAndStatus(
                                    request.getCustomerId(),
                                    "PERSONAL",
                                    "ACTIVE"
                            )
                            .count()
                            .flatMap(count -> {
                                if (count >= MAX_PERSONAL_CREDITS) {
                                    return Mono.error(new IllegalStateException("El cliente personal ya tiene un crédito personal activo."));
                                }

                                // Si pasa la validacion crea el crédito
                                Credit credit = new Credit(
                                        UUID.randomUUID().toString(),
                                        "PERSONAL",
                                        request.getCustomerId(),
                                        request.getAmount(),
                                        request.getOutstandingBalance(),
                                        "ACTIVE"
                                );
                                credit.setDueDate(LocalDate.now().plusMonths(1));
                                credit.setOverdue(false);
                                return creditRepository.save(credit);
                            });
                });
    }

    @Override
    public Mono<Credit> createBusinessCredit(CreditRequest request) {
        // Primero verificar si tiene deuda vencida
        return hasOverdueDebt(request.getCustomerId())
                .flatMap(hasOverdue -> {
                    if (hasOverdue) {
                        return Mono.error(new BusinessException("El cliente tiene deudas vencidas. No puede adquirir nuevos créditos."));
                    }

                    //límite dinámico
                    final int MAX_BUSINESS_CREDITS = 5;

                    return creditRepository.findByCustomerIdAndCreditTypeAndStatus(
                                    request.getCustomerId(),
                                    "EMPRESARIAL",
                                    "ACTIVE"
                            )
                            .count()
                            .flatMap(count -> {
                                if (count >= MAX_BUSINESS_CREDITS) {
                                    return Mono.error(new IllegalStateException("El cliente empresarial ya tiene el máximo permitido de créditos activos."));
                                }

                                // Se crea el credito si pasa la validacion
                                Credit credit = new Credit(
                                        UUID.randomUUID().toString(),
                                        "EMPRESARIAL",
                                        request.getCustomerId(),
                                        request.getAmount(),
                                        request.getOutstandingBalance(),
                                        "ACTIVE"
                                );
                                credit.setDueDate(LocalDate.now().plusMonths(1));
                                credit.setOverdue(false);
                                return creditRepository.save(credit);
                            });
                });
    }

    @Override
    public Mono<Credit> payCredit(String creditId, BigDecimal paymentAmount) {
        return creditRepository.findById(creditId)
                .flatMap(credit -> {
                    BigDecimal outstandingBalance = credit.getOutstandingBalance();

                    // Validación: Pago excede la deuda
                    if (paymentAmount.compareTo(outstandingBalance) > 0) {
                        return Mono.error(new IllegalStateException("El pago excede el saldo pendiente."));
                    }

                    // Actualizar saldo
                    BigDecimal newBalance = outstandingBalance.subtract(paymentAmount);
                    credit.setOutstandingBalance(newBalance);

                    // Si hay pago y estaba vencido, actualizar estado
                    if (credit.isOverdue()) {
                        credit.setOverdue(false);
                        // Establecer nueva fecha de vencimiento un mes después
                        credit.setDueDate(LocalDate.now().plusMonths(1));
                    }

                    // Si el saldo llega a 0, cambiar estado a PAID
                    if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
                        credit.setStatus("PAID");
                    }

                    return creditRepository.save(credit);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Crédito no encontrado con id: " + creditId)));
    }

    @Override
    public Mono<Credit> payThirdPartyCredit(String creditId, String payerCustomerId, BigDecimal paymentAmount) {
        return creditRepository.findById(creditId)
                .flatMap(credit -> {
                    BigDecimal outstandingBalance = credit.getOutstandingBalance();

                    // Validación: Pago excede la deuda
                    if (paymentAmount.compareTo(outstandingBalance) > 0) {
                        return Mono.error(new IllegalStateException("El pago excede el saldo pendiente."));
                    }

                    // Registrar información de pagador de terceros
                    credit.setLastPaymentBy(payerCustomerId);
                    credit.setLastPaymentDate(LocalDate.now());

                    // Actualizar saldo
                    BigDecimal newBalance = outstandingBalance.subtract(paymentAmount);
                    credit.setOutstandingBalance(newBalance);

                    // Si hay pago y estaba vencido, actualizar estado
                    if (credit.isOverdue()) {
                        credit.setOverdue(false);
                        // Establecer nueva fecha de vencimiento un mes después
                        credit.setDueDate(LocalDate.now().plusMonths(1));
                    }

                    // Si el saldo llega a 0, cambiar estado a PAID
                    if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
                        credit.setStatus("PAID");
                    }

                    return creditRepository.save(credit);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Crédito no encontrado con id: " + creditId)));
    }

    @Override
    public Mono<Boolean> hasOverdueDebt(String customerId) {
        // Buscar todos los créditos activos del cliente que estén marcados como vencidos
        return creditRepository.findByCustomerIdAndOverdue(customerId, true)
                .hasElements();
    }

    @Override
    public Mono<Void> updateOverdueStatus() {
        LocalDate today = LocalDate.now();

        return creditRepository.findByStatusAndDueDateBefore("ACTIVE", today)
                .flatMap(credit -> {
                    credit.setOverdue(true);
                    return creditRepository.save(credit);
                })
                .then();
    }
}