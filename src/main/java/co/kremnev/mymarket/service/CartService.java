package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.dto.SessionCart;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    Mono<SessionCart> getCart(WebSession session);
    Mono<Void> removeItem(WebSession session, long itemId);
    Mono<Void> updateItemCount(WebSession session, long itemId, String action);
    Flux<CartItem> getCartItems(WebSession session);
    Mono<BigDecimal> getCartTotal(WebSession session);
    Mono<Void> clear(WebSession session);
}
