package co.kremnev.mymarket.service;

import co.kremnev.mymarket.model.Order;
import co.kremnev.payment.client.api.PaymentsApi;
import co.kremnev.payment.client.model.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OrderProcessingServiceImpl implements OrderProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingServiceImpl.class);

    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentsApi paymentsApi;

    public OrderProcessingServiceImpl(CartService cartService, OrderService orderService, PaymentsApi paymentsApi) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.paymentsApi = paymentsApi;
    }

    @Override
    public Mono<Order> checkout(String sessionId) {
        return Mono.zip(
                cartService.getCartItems(sessionId).collectList(),
                cartService.getCartTotal(sessionId)
        ).flatMap(tuple -> {
            var items = tuple.getT1();
            var total = tuple.getT2();
            return paymentsApi.processPayment(new PaymentRequest().userId(sessionId).amount(total))
                    .flatMap(paymentResult -> orderService.create(items))
                    .flatMap(order -> cartService.clear(sessionId).thenReturn(order));
        }).doOnError(error -> logger.error("Checkout failed for session {}: {}", sessionId, error.getMessage()));
    }
}
