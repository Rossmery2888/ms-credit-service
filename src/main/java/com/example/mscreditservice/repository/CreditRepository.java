package com.example.mscreditservice.repository;


import com.example.mscreditservice.model.Credit;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {
    Flux<Credit> findByCustomerId(String customerId);
    Flux<Credit> findByCustomerIdAndCreditTypeAndStatus(String customerId, String creditType, String status);
    Flux<Credit> findByCustomerIdAndOverdue(String customerId, boolean overdue);
    Flux<Credit> findByStatusAndDueDateBefore(String status, LocalDate date);
}