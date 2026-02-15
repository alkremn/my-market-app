package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.model.SecurityUser;
import co.kremnev.payment.client.api.PaymentsApi;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import co.kremnev.mymarket.dto.ItemDto;
import co.kremnev.mymarket.dto.Request.CartCommandRequest;
import co.kremnev.mymarket.service.CartService;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

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
                                                 @AuthenticationPrincipal SecurityUser principal) {
        return cartService.updateItemCount(principal.getId(), request.id(), request.action())
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
            @AuthenticationPrincipal SecurityUser principal
    ) {
        return cartService.updateItemCount(principal.getId(), request.id(), request.action())
                .then(Mono.just(Rendering.redirectTo("/items/" + id).build()));
    }

    @GetMapping("/cart/items")
    public Mono<Rendering> getItems(@AuthenticationPrincipal SecurityUser principal) {
        Flux<ItemDto> itemsFlux = cartService.getCartItems(principal.getId())
                .map(cartItem -> ItemDto.from(cartItem.getItem(), cartItem.getQuantity()));

        return Mono.zip(
                itemsFlux.collectList(),
                cartService.getCartTotal(principal.getId()),
                paymentsApi.getBalance(principal.getId())
                        .doOnNext(balance -> logger.info("Balance : {}", balance))
                        .doOnError(error -> logger.error("Error getting balance: {}", error.getMessage()))
                ).map(tuple -> buildCartRendering(tuple.getT1(), tuple.getT2(), tuple.getT3().getBalance()));
    }

    @PostMapping("/cart/items")
    public Mono<Rendering> updateItems(@ModelAttribute @Valid CartCommandRequest request,
                                       @AuthenticationPrincipal SecurityUser principal) {
        Flux<ItemDto> itemsFlux = cartService.updateItemCount(principal.getId(), request.id(), request.action())
                .thenMany(cartService.getCartItems(principal.getId()))
                .map(cartItem -> ItemDto.from(cartItem.getItem(), cartItem.getQuantity()));

        return Mono.zip(
                itemsFlux.collectList(),
                cartService.getCartTotal(principal.getId()),
                paymentsApi.getBalance(principal.getId())
                        .doOnNext(balance -> logger.info("Balance : {}", balance))
                        .doOnError(error -> logger.info("Error getting balance: {}", error.getMessage()))
            ).map(tuple -> buildCartRendering(tuple.getT1(), tuple.getT2(), tuple.getT3().getBalance()));
    }

    private Rendering buildCartRendering(List<ItemDto> items, BigDecimal total, BigDecimal balance) {
        boolean canBuy = balance != null && balance.compareTo(total) >= 0;
        boolean insufficientFunds = balance != null && !canBuy && !items.isEmpty();

        return Rendering.view("cart")
                .modelAttribute("items", items)
                .modelAttribute("total", total)
                .modelAttribute("canBuy", canBuy)
                .modelAttribute("insufficientFunds", insufficientFunds)
                .build();
    }
}