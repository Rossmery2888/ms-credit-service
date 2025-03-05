package com.nttdata.bankapp.mscreditservice.service.impl;

import com.nttdata.bankapp.mscreditservice.client.CustomerService;
import com.nttdata.bankapp.mscreditservice.dto.CreditBalanceDto;
import com.nttdata.bankapp.mscreditservice.dto.CreditDto;


import com.nttdata.bankapp.mscreditservice.exception.CreditNotFoundException;
import com.nttdata.bankapp.mscreditservice.exception.CustomerNotFoundException;
import com.nttdata.bankapp.mscreditservice.exception.InvalidCreditTypeException;
import com.nttdata.bankapp.mscreditservice.model.Credit;
import com.nttdata.bankapp.mscreditservice.model.enums.CreditType;
import com.nttdata.bankapp.mscreditservice.model.enums.CustomerType;
import com.nttdata.bankapp.mscreditservice.repository.CreditRepository;
import com.nttdata.bankapp.mscreditservice.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementación de los servicios para operaciones con créditos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;
    private final CustomerService customerService;

    @Override
    public Flux<CreditDto> findAll() {
        log.info("Finding all credits");
        return creditRepository.findAll()
                .map(this::mapToDto);
    }

    @Override
    public Mono<CreditDto> findById(String id) {
        log.info("Finding credit by id: {}", id);
        return creditRepository.findById(id)
                .map(this::mapToDto)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Credit not found with id: " + id)));
    }

    @Override
    public Flux<CreditDto> findByCustomerId(String customerId) {
        log.info("Finding credits by customer id: {}", customerId);
        return creditRepository.findByCustomerId(customerId)
                .map(this::mapToDto);
    }

    @Override
    public Mono<CreditDto> findByCreditNumber(String creditNumber) {
        log.info("Finding credit by credit number: {}", creditNumber);
        return creditRepository.findByCreditNumber(creditNumber)
                .map(this::mapToDto)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Credit not found with credit number: " + creditNumber)));
    }

    @Override
    public Mono<CreditDto> save(CreditDto creditDto) {
        log.info("Saving new credit: {}", creditDto);

        // Verificar si el cliente existe
        return customerService.customerExists(creditDto.getCustomerId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new CustomerNotFoundException("Customer not found with id: " + creditDto.getCustomerId()));
                    }

                    // Validar reglas de negocio según tipo de cliente y crédito
                    return validateCreditRules(creditDto)
                            .flatMap(valid -> {
                                Credit credit = mapToEntity(creditDto);

                                // Generar número de crédito
                                credit.setCreditNumber(generateCreditNumber());

                                // Establecer fechas y montos
                                credit.setStartDate(LocalDate.now());
                                credit.setDueDate(LocalDate.now().plusMonths(credit.getTerm()));
                                credit.setRemainingAmount(credit.getAmount());
                                credit.setCreatedAt(LocalDateTime.now());
                                credit.setUpdatedAt(LocalDateTime.now());

                                return creditRepository.save(credit).map(this::mapToDto);
                            });
                });
    }

    @Override
    public Mono<CreditDto> update(String id, CreditDto creditDto) {
        log.info("Updating credit id: {}", id);
        return creditRepository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Credit not found with id: " + id)))
                .flatMap(existingCredit -> {
                    // No permitir cambiar campos críticos como cliente o tipo
                    if (creditDto.getCustomerId() != null && !creditDto.getCustomerId().equals(existingCredit.getCustomerId())) {
                        return Mono.error(new IllegalArgumentException("Cannot change credit owner"));
                    }

                    if (creditDto.getCustomerType() != null && !creditDto.getCustomerType().equals(existingCredit.getCustomerType())) {
                        return Mono.error(new IllegalArgumentException("Cannot change customer type"));
                    }

                    if (creditDto.getType() != null && !creditDto.getType().equals(existingCredit.getType())) {
                        return Mono.error(new IllegalArgumentException("Cannot change credit type"));
                    }

                    // Actualizar otros campos según sea necesario
                    if (creditDto.getInterestRate() != null) {
                        existingCredit.setInterestRate(creditDto.getInterestRate());
                    }

                    if (creditDto.getTerm() != null) {
                        existingCredit.setTerm(creditDto.getTerm());
                        existingCredit.setDueDate(existingCredit.getStartDate().plusMonths(creditDto.getTerm()));
                    }

                    existingCredit.setUpdatedAt(LocalDateTime.now());

                    return creditRepository.save(existingCredit);
                })
                .map(this::mapToDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.info("Deleting credit id: {}", id);
        return creditRepository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Credit not found with id: " + id)))
                .flatMap(credit -> creditRepository.deleteById(id));
    }

    @Override
    public Mono<CreditBalanceDto> getBalance(String id) {
        log.info("Getting balance for credit id: {}", id);
        return creditRepository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Credit not found with id: " + id)))
                .map(credit -> CreditBalanceDto.builder()
                        .creditId(credit.getId())
                        .creditNumber(credit.getCreditNumber())
                        .totalAmount(credit.getAmount())
                        .remainingAmount(credit.getRemainingAmount())
                        .paidAmount(credit.getAmount().subtract(credit.getRemainingAmount()))
                        .build());
    }

    @Override
    public Mono<CreditDto> makePayment(String id, BigDecimal amount) {
        log.info("Making payment to credit id: {} with amount: {}", id, amount);
        return creditRepository.findById(id)
                .switchIfEmpty(Mono.error(new CreditNotFoundException("Credit not found with id: " + id)))
                .flatMap(credit -> {
                    // Validar que el monto no sea mayor que el saldo pendiente
                    if (amount.compareTo(credit.getRemainingAmount()) > 0) {
                        return Mono.error(new IllegalArgumentException("Payment amount cannot exceed remaining balance"));
                    }

                    // Actualizar saldo pendiente
                    BigDecimal newRemainingAmount = credit.getRemainingAmount().subtract(amount);
                    credit.setRemainingAmount(newRemainingAmount);
                    credit.setUpdatedAt(LocalDateTime.now());

                    return creditRepository.save(credit);
                })
                .map(this::mapToDto);
    }

    /**
     * Valida las reglas de negocio para la creación de créditos.
     * @param creditDto DTO con los datos del crédito
     * @return Mono<Boolean> true si es válido, error en caso contrario
     */
    private Mono<Boolean> validateCreditRules(CreditDto creditDto) {
        // Validar reglas según tipo de cliente
        if (creditDto.getCustomerType() == CustomerType.PERSONAL) {
            // Cliente personal solo puede tener un crédito personal
            if (creditDto.getType() == CreditType.PERSONAL) {
                return creditRepository.countByCustomerIdAndType(creditDto.getCustomerId(), CreditType.PERSONAL)
                        .flatMap(count -> {
                            if (count > 0) {
                                return Mono.error(new IllegalArgumentException(
                                        "Personal customers can only have one personal credit"));
                            }
                            return Mono.just(true);
                        });
            }

            // Cliente personal no puede tener créditos empresariales
            if (creditDto.getType() == CreditType.BUSINESS) {
                return Mono.error(new InvalidCreditTypeException(
                        "Personal customers cannot have business credits"));
            }
        }

        // Cliente empresarial puede tener múltiples créditos empresariales
        return Mono.just(true);
    }

    /**
     * Genera un número de crédito aleatorio.
     * @return String con el número de crédito
     */
    private String generateCreditNumber() {
        return "CR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Convierte una entidad Credit a DTO.
     * @param credit Entidad a convertir
     * @return CreditDto
     */
    private CreditDto mapToDto(Credit credit) {
        return CreditDto.builder()
                .id(credit.getId())
                .creditNumber(credit.getCreditNumber())
                .customerId(credit.getCustomerId())
                .customerType(credit.getCustomerType())
                .type(credit.getType())
                .amount(credit.getAmount())
                .remainingAmount(credit.getRemainingAmount())
                .interestRate(credit.getInterestRate())
                .term(credit.getTerm())
                .startDate(credit.getStartDate())
                .dueDate(credit.getDueDate())
                .build();
    }

    /**
     * Convierte un DTO a entidad Credit.
     * @param creditDto DTO a convertir
     * @return Credit
     */
    private Credit mapToEntity(CreditDto creditDto) {
        return Credit.builder()
                .customerId(creditDto.getCustomerId())
                .customerType(creditDto.getCustomerType())
                .type(creditDto.getType())
                .amount(creditDto.getAmount())
                .interestRate(creditDto.getInterestRate())
                .term(creditDto.getTerm())
                .build();
    }
}