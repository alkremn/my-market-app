package co.kremnev.mymarket.controller;

import co.kremnev.payment.client.api.PaymentsApi;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import co.kremnev.mymarket.dto.ItemDto;
import co.kremnev.mymarket.dto.Request.CartCommandRequest;
import co.kremnev.mymarket.service.CartService;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@Validated
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final PaymentsApi paymentsApi;

    @Autowired
    public CartController(CartService cartService, PaymentsApi paymentsApi) {
        this.cartService = cartService;
        this.paymentsApi = paymentsApi;
    }

    @PostMapping("/items")
    public Mono<Rendering> addOrRemoveItemInCart(@ModelAttribute @Valid CartCommandRequest request,
                                                 WebSession session) {
        return cartService.updateItemCount(session.getId(), request.id(), request.action())
                .then(Mono.just(Rendering.redirectTo(
                        "/items?search=" + request.search() +
                                "&sort=" + request.sort() +
                                "&pageNumber=" + request.pageNumber() +
                                "&pageSize=" + request.pageSize()).build()));
    }

    @PostMapping("/items/{id}")
    public Mono<Rendering> addOrRemoveItemInCartById(
            @PathVariable(required = false) String id,
            @ModelAttribute @Valid CartCommandRequest request,
            WebSession session
    ) {
        return cartService.updateItemCount(session.getId(), request.id(), request.action())
                .then(Mono.just(Rendering.redirectTo("/items/" + id).build()));
    }

    @GetMapping("/cart/items")
    public Mono<Rendering> getItems(WebSession session) {
        Flux<ItemDto> itemsFlux = cartService.getCartItems(session.getId())
                .map(cartItem -> ItemDto.from(cartItem.getItem(), cartItem.getQuantity()));

        return Mono.zip(
                itemsFlux.collectList(),
                cartService.getCartTotal(session.getId()),
                paymentsApi.getBalance(session.getId())
                        .doOnNext(balance -> logger.info("Balance : {}", balance))
                        .doOnError(error -> logger.error("Error getting balance: {}", error.getMessage()))
                ).map(tuple -> {
            assert tuple.getT3().getBalance() != null;
            return Rendering.view("cart")
                    .modelAttribute("items", tuple.getT1())
                    .modelAttribute("total", tuple.getT2())
                    .modelAttribute("canBuy", tuple.getT3().getBalance().compareTo(tuple.getT2()) >= 0)
                    .build();
        });
    }

    @PostMapping("/cart/items")
    public Mono<Rendering> updateItems(@ModelAttribute @Valid CartCommandRequest request, WebSession session) {
        Flux<ItemDto> itemsFlux = cartService.updateItemCount(session.getId(), request.id(),  request.action())
                .thenMany(cartService.getCartItems(session.getId()))
                .map(cartItem -> ItemDto.from(cartItem.getItem(), cartItem.getQuantity()));

        return Mono.zip(
                itemsFlux.collectList(),
                cartService.getCartTotal(session.getId()),
                paymentsApi.getBalance(session.getId())
                        .doOnNext(balance -> logger.info("Balance : {}", balance))
                        .doOnError(error -> logger.info("Error getting balance: {}", error.getMessage()))
            ).map(tuple -> {
            assert tuple.getT3().getBalance() != null;
            return Rendering.view("cart")
                .modelAttribute("items", tuple.getT1())
                .modelAttribute("total", tuple.getT2())
                .modelAttribute("canBuy", tuple.getT3().getBalance().compareTo(tuple.getT2()) >= 0)
                .build();
        });
    }
}