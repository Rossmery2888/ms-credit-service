package com.example.mscreditservice.repository;

import com.example.mscreditservice.model.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {
    Flux<Credit> findByCustomerId(String customerId);
    Flux<Credit> findByCustomerIdAndCreditTypeAndStatus(String customerId, String creditType, String status);
}
