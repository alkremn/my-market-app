package co.kremnev.mymarket.service;

import co.kremnev.mymarket.model.Order;
import reactor.core.publisher.Mono;

public interface OrderProcessingService {
    Mono<Order> checkout(Long userId);
}
