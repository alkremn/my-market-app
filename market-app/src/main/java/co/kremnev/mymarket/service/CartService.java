package co.kremnev.mymarket.service;

import co.kremnev.mymarket.model.Cart;
import co.kremnev.mymarket.model.CartAction;
import co.kremnev.mymarket.model.CartItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CartService {
    Mono<Cart> getCart(String sessionId);
    Mono<Void> updateItemCount(String sessionId, Long itemId, CartAction action);
    Flux<CartItem> getCartItems(String sessionId);
    Mono<BigDecimal> getCartTotal(String sessionId);
    Mono<Void> clear(String sessionId);
}
