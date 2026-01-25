package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.dto.SessionCart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class CartServiceImpl implements CartService {
    private static final String CART_SESSION_KEY = "SHOPPING_CART";
    private final ItemService itemService;

    public CartServiceImpl(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public Mono<SessionCart> getCart(WebSession session) {
        SessionCart cart = session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new SessionCart();
            session.getAttributes().put(CART_SESSION_KEY, cart);
        }
        return Mono.just(cart);
    }

    @Override
    public Mono<Void> removeItem(WebSession session, long itemId) {
        return getCart(session)
                .doOnNext(cart -> cart.removeItem(itemId))
                .then();
    }

    @Override
    public Mono<Void> updateItemCount(WebSession session, long itemId, String action) {
        return getCart(session)
                .doOnNext(cart -> {
                    if (action.equals("DELETE")) {
                        cart.removeItem(itemId);
                    } else {
                        var itemCount = cart.getItemCountById(itemId);
                        var newCount = itemCount + (action.equals("PLUS") ? 1 : -1);
                        if (newCount < 0) {
                            cart.removeItem(itemId);
                        } else {
                            cart.updateItem(itemId, newCount);
                        }
                    }
                })
                .then();
    }

    @Override
    public Flux<CartItem> getCartItems(WebSession session) {
        return getCart(session)
                .flatMapMany(cart -> Flux.fromIterable(cart.getItems().entrySet()))
                .flatMap(cartEntry ->
                        itemService.getById(cartEntry.getKey())
                        .map(item -> new CartItem(item, cartEntry.getValue())));
    }

    @Override
    public Mono<BigDecimal> getCartTotal(WebSession session) {
        return getCartItems(session)
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .defaultIfEmpty(BigDecimal.ZERO);
    }

    @Override
    public Mono<Void> clear(WebSession session) {
        return getCart(session)
            .doOnNext(SessionCart::clear)
                .then();
    }
}
