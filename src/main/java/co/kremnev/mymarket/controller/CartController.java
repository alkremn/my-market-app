package co.kremnev.mymarket.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public Mono<Rendering> addOrRemoveItemInCart(@ModelAttribute @Valid CartCommandRequest request,
                                                 WebSession session) {
        return cartService.updateItemCount(session, request.id(), request.action())
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
        return cartService.updateItemCount(session, request.id(), request.action())
                .then(Mono.just(Rendering.redirectTo("redirect:/items/" + id).build()));
    }

    @GetMapping("/cart/items")
    public Mono<Rendering> getItems(WebSession session) {
        Flux<ItemDto> itemsFlux = cartService.getCartItems(session)
                .map(cartItem -> ItemDto.from(cartItem.item(), cartItem.quantity()));

        return Mono.zip(
                itemsFlux.collectList(),
                cartService.getCartTotal(session)
                ).map(tuple -> Rendering.view("cart")
                        .modelAttribute("items", tuple.getT1())
                        .modelAttribute("total", tuple.getT2())
                        .build());
    }

    @PostMapping("/cart/items")
    public Mono<Rendering> updateItems(@RequestParam Long id, @RequestParam String action,
                                    Model model, WebSession session) {
        Flux<ItemDto> itemsFlux = cartService.updateItemCount(session, id,  action)
                .thenMany(cartService.getCartItems(session))
                .map(cartItem -> ItemDto.from(cartItem.item(), cartItem.quantity()));

        return Mono.zip(
                itemsFlux.collectList(),
                cartService.getCartTotal(session)
            ).map(tuple -> Rendering.view("cart")
                .modelAttribute("items", tuple.getT1())
                .modelAttribute("total", tuple.getT2())
                .build());
    }
}