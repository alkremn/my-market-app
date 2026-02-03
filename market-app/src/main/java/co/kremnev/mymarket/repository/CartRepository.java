package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.Cart;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CartRepository extends ReactiveCrudRepository<Cart, Long> {
    Mono<Cart> findBySessionId(String sessionId);
}
