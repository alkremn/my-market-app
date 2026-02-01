package co.kremnev.payment.repository;

import co.kremnev.payment.model.Balance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface PaymentRepository extends ReactiveCrudRepository<Balance, UUID> {

}
