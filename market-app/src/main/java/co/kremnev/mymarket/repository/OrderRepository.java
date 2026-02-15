package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
    Flux<Order> findAllByUserId(Long userId);
    Mono<Order> findByIdAndUserId(Long id, Long userId);
}
