package co.kremnev.payment.repository;

import co.kremnev.payment.model.Balance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PaymentRepository extends ReactiveCrudRepository<Balance, Long> {

}
