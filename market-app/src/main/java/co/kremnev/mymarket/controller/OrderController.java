package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.OrderDto;
import co.kremnev.mymarket.service.OrderProcessingService;
import co.kremnev.mymarket.service.OrderService;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Controller
@Validated
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final OrderProcessingService orderProcessingService;

    @Autowired
    public OrderController(OrderService orderService, OrderProcessingService orderProcessingService) {
        this.orderService = orderService;
        this.orderProcessingService = orderProcessingService;
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
    public Mono<Rendering> getOrderById(@PathVariable @Min(1) long id, @RequestParam(required = false) String newOrder) {
        return orderService.getById(id)
                .map(order -> Rendering.view("order")
                        .modelAttribute("order", OrderDto.from(order)).build())
                .switchIfEmpty(Mono.just(Rendering.view("notfound").build()));
    }

    @PostMapping("/buy")
    public Mono<Rendering> buy(WebSession session) {
        return orderProcessingService.checkout(session.getId())
                .map(order -> Rendering.redirectTo("orders/" + order.getId() + "?newOrder=true").build())
                .onErrorResume(e -> {
                    logger.error("Checkout failed for session {}: {}", session.getId(), e.getMessage());
                    String message = (e instanceof WebClientResponseException.BadRequest)
                            ? "Оплата не прошла. Недостаточно средств на балансе."
                            : "Произошла ошибка при оформлении заказа. Попробуйте позже.";
                    return Mono.just(Rendering.view("checkout-error")
                            .modelAttribute("errorMessage", message)
                            .build());
                });
    }
}