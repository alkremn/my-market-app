package co.kremnev.mymarket.service;

import co.kremnev.mymarket.model.CartItem;
import co.kremnev.mymarket.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderService {
    Flux<Order> getAll();
    Mono<Order> getById(long id);
    Mono<Order> create(List<CartItem> cartItems);
}
