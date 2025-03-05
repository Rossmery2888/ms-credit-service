package com.nttdata.bankapp.mscreditservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Cliente para comunicarse con el microservicio de clientes.
 */
@Service
public class CustomerService {

    private final WebClient webClient;

    public CustomerService(@Value("${app.customer-service-url}") String customerServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(customerServiceUrl)
                .build();
    }

    /**
     * Verifica si un cliente existe por su ID.
     * @param customerId ID del cliente
     * @return Mono<Boolean> true si existe, false en caso contrario
     */
    public Mono<Boolean> customerExists(String customerId) {
        return webClient.get()
                .uri("/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(Object.class)
                .map(response -> true)
                .onErrorResume(e -> Mono.just(false));
    }

    /**
     * Obtiene el tipo de un cliente por su ID.
     * @param customerId ID del cliente
     * @return Mono<String> tipo de cliente
     */
    public Mono<String> getCustomerType(String customerId) {
        return webClient.get()
                .uri("/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(CustomerDto.class)
                .map(customer -> customer.getType().toString());
    }

    // DTO interno para mapear la respuesta del servicio de clientes
    @lombok.Data
    private static class CustomerDto {
        private String id;
        private CustomerTypeEnum type;
    }

    private enum CustomerTypeEnum {
        PERSONAL, BUSINESS
    }
}