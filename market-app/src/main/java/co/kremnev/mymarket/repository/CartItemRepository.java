package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.CartItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {
    Flux<CartItem> findAllByCartId(Long cartId);
    Mono<CartItem> findByCartIdAndItemId(Long cartId, Long itemId);
}
