package com.nttdata.bankapp.mscreditservice.repository;
import com.nttdata.bankapp.mscreditservice.model.Credit;
import com.nttdata.bankapp.mscreditservice.model.enums.CreditType;
import com.nttdata.bankapp.mscreditservice.model.enums.CustomerType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio para operaciones CRUD en la colección de créditos.
 */
@Repository
public interface CreditRepository extends ReactiveMongoRepository<Credit, String> {
    Flux<Credit> findByCustomerId(String customerId);
    Mono<Credit> findByCreditNumber(String creditNumber);
    Flux<Credit> findByCustomerIdAndType(String customerId, CreditType type);
    Flux<Credit> findByCustomerIdAndCustomerType(String customerId, CustomerType customerType);
    Mono<Long> countByCustomerIdAndType(String customerId, CreditType type);
}