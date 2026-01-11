package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
