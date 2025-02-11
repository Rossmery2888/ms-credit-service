package com.example.mscreditservice.repository;

import com.example.mscreditservice.model.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {
    Flux<Credit> findByCustomerId(String customerId);
    Mono<Long> countByCustomerIdAndCustomerType(String customerId, String customerType);
}