package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.ItemDto;
import co.kremnev.mymarket.dto.Request.CartCommandRequest;
import co.kremnev.mymarket.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping(value = {"/items", "/items/{id}"})
    public String addOrRemoveToCart(
            @PathVariable(required = false) String id,
            @ModelAttribute CartCommandRequest request,
            HttpSession session) {

        String action = request.action();

        if (request.id() != null && action != null) {
            cartService.updateItemCount(session, request.id(), action.equals("PLUS") ? 1 : -1);
        }

        if (id == null) {
            return "redirect:/items?search=" + request.search() +
                    "&sort=" + request.sort() +
                    "&pageNumber=" + request.pageNumber() +
                    "&pageSize=" + request.pageSize();
        } else {
            return "redirect:/items/" + id;
        }

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
        cartService.removeItem(session, id);
        var cartItems = cartService.getCartItems(session);
        model.addAttribute("items", cartItems.stream()
                .map(item -> ItemDto.from(item.item(), item.quantity())).toList());
        model.addAttribute("total", cartService.getCartTotal(cartItems));
        return "cart";
    }
}