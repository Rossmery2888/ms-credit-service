package com.nttdata.bankapp.mscreditservice.controller;

import com.nttdata.bankapp.mscreditservice.dto.CreditBalanceDto;
import com.nttdata.bankapp.mscreditservice.dto.CreditDto;
import com.nttdata.bankapp.mscreditservice.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * Controlador para operaciones con créditos.
 */
@RestController
@RequestMapping("/credits")
@RequiredArgsConstructor
@Slf4j
public class CreditController {

    private final CreditService creditService;

    /**
     * Obtiene todos los créditos.
     * @return Flux de CreditDto
     */
    @GetMapping
    public Flux<CreditDto> getAll() {
        log.info("GET /credits");
        return creditService.findAll();
    }

    /**
     * Obtiene un crédito por su ID.
     * @param id ID del crédito
     * @return Mono de CreditDto
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CreditDto>> getById(@PathVariable String id) {
        log.info("GET /credits/{}", id);
        return creditService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene los créditos de un cliente.
     * @param customerId ID del cliente
     * @return Flux de CreditDto
     */
    @GetMapping("/customer/{customerId}")
    public Flux<CreditDto> getByCustomerId(@PathVariable String customerId) {
        log.info("GET /credits/customer/{}", customerId);
        return creditService.findByCustomerId(customerId);
    }

    /**
     * Obtiene un crédito por su número.
     * @param creditNumber Número de crédito
     * @return Mono de CreditDto
     */
    @GetMapping("/number/{creditNumber}")
    public Mono<ResponseEntity<CreditDto>> getByCreditNumber(@PathVariable String creditNumber) {
        log.info("GET /credits/number/{}", creditNumber);
        return creditService.findByCreditNumber(creditNumber)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Consulta el saldo de un crédito.
     * @param id ID del crédito
     * @return Mono de CreditBalanceDto
     */
    @GetMapping("/{id}/balance")
    public Mono<ResponseEntity<CreditBalanceDto>> getBalance(@PathVariable String id) {
        log.info("GET /credits/{}/balance", id);
        return creditService.getBalance(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo crédito.
     * @param creditDto DTO con los datos del crédito
     * @return Mono de CreditDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreditDto> create(@Valid @RequestBody CreditDto creditDto) {
        log.info("POST /credits");
        return creditService.save(creditDto);
    }

    /**
     * Actualiza un crédito existente.
     * @param id ID del crédito
     * @param creditDto DTO con los datos a actualizar
     * @return Mono de CreditDto
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CreditDto>> update(@PathVariable String id, @Valid @RequestBody CreditDto creditDto) {
        log.info("PUT /credits/{}", id);
        return creditService.update(id, creditDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un crédito.
     * @param id ID del crédito
     * @return Mono<Void>
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        log.info("DELETE /credits/{}", id);
        return creditService.delete(id);
    }

    /**
     * Realiza un pago a un crédito.
     * @param id ID del crédito
     * @param amount Monto a pagar
     * @return Mono de CreditDto
     */
    @PutMapping("/{id}/payment")
    public Mono<ResponseEntity<CreditDto>> makePayment(
            @PathVariable String id,
            @RequestParam BigDecimal amount) {
        log.info("PUT /credits/{}/payment with amount: {}", id, amount);
        return creditService.makePayment(id, amount)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}