package co.kremnev.mymarket.service;

import co.kremnev.mymarket.model.Cart;
import co.kremnev.mymarket.model.CartAction;
import co.kremnev.mymarket.model.CartItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CartService {
    Mono<Cart> getCart(Long userId);
    Mono<Void> updateItemCount(Long userId, Long itemId, CartAction action);
    Flux<CartItem> getCartItems(Long userId);
    Mono<BigDecimal> getCartTotal(Long userId);
    Mono<Void> clear(Long userId);
}
