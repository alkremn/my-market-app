package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {}
