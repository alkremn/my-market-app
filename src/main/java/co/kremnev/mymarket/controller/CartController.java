package co.kremnev.mymarket.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import co.kremnev.mymarket.dto.ItemDto;
import co.kremnev.mymarket.dto.Request.CartCommandRequest;
import co.kremnev.mymarket.service.CartService;

@Controller
@Validated
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public String addOrRemoveItemInCart(
            @ModelAttribute @Valid CartCommandRequest request,
            HttpSession session
    ) {
        cartService.updateItemCount(session, request.id(), request.action());

        return "redirect:/items?search=" + request.search() +
                "&sort=" + request.sort() +
                "&pageNumber=" + request.pageNumber() +
                "&pageSize=" + request.pageSize();
    }

    @PostMapping("/items/{id}")
    public String addOrRemoveItemInCartById(
            @PathVariable(required = false) String id,
            @ModelAttribute @Valid CartCommandRequest request,
            HttpSession session
    ) {
        cartService.updateItemCount(session, request.id(), request.action());
        return "redirect:/items/" + id;
    }

    @GetMapping("/cart/items")
    public String getItems(Model model, HttpSession session) {
        var cartItems = cartService.getCartItems(session);
        model.addAttribute("items", cartItems.stream()
                .map(item -> ItemDto.from(item.item(), item.quantity())).toList());
        model.addAttribute("total", cartService.getCartTotal(cartItems));
        return "cart";
    }

    @PostMapping("/cart/items")
    public String getItems(@RequestParam long id, @RequestParam String action, Model model, HttpSession session) {
        cartService.updateItemCount(session, id,  action);
        var cartItems = cartService.getCartItems(session);
        model.addAttribute("items", cartItems.stream()
                .map(item -> ItemDto.from(item.item(), item.quantity())).toList());
        model.addAttribute("total", cartService.getCartTotal(cartItems));
        return "cart";
    }
}