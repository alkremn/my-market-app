package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.OrderDto;
import co.kremnev.mymarket.service.CartService;
import co.kremnev.mymarket.service.OrderService;
import co.kremnev.payment.client.api.PaymentsApi;
import co.kremnev.payment.client.model.PaymentRequest;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Controller
@Validated
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final CartService cartService;
    private final PaymentsApi paymentsApi;

    @Autowired
    public OrderController(OrderService orderService, CartService cartService, PaymentsApi paymentsApi) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.paymentsApi = paymentsApi;
    }

    @GetMapping("/orders")
    public Mono<Rendering> getOrders() {
        return Mono.just(
                Rendering.view("orders")
                        .modelAttribute("orders", orderService.getAll().map(OrderDto::from))
                        .build()
        );
    }

    @GetMapping("/orders/{id}")
    public Mono<Rendering> getOrderById(@PathVariable @Min(1) long id, @RequestParam(required = false) String newOrder, Model model) {
        return orderService.getById(id)
                .map(order -> Rendering.view("order")
                        .modelAttribute("order", OrderDto.from(order)).build())
                .switchIfEmpty(Mono.just(Rendering.view("notfound").build()));
    }

    @PostMapping("/buy")
    public Mono<String> buy(WebSession session) {
        return Mono.zip(
                cartService.getCartItems(session).collectList(),
                cartService.getCartTotal(session)
        ).flatMap(tuple -> {
            var items = tuple.getT1();
            var total = tuple.getT2();
            return paymentsApi.processPayment(new PaymentRequest().userId(session.getId()).amount(total))
                    .flatMap(paymentResult -> orderService.create(items))
                    .flatMap(order -> cartService.clear(session).thenReturn(order))
                    .map(order -> "redirect:orders/" + order.getId() + "?newOrder=true");
        }).doOnError(error -> logger.info("Error creating payment: {}", error.getMessage()));
    }
}