package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.OrderDto;
import co.kremnev.mymarket.service.CartService;
import co.kremnev.mymarket.service.OrderService;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Validated
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping("/orders")
    public String getOrders(Model model) {
        var allOrders = orderService.getAllOrders();
        model.addAttribute("orders", allOrders.stream().map(OrderDto::from).toList());
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String getOrderById(@PathVariable @Min(1) long id, @RequestParam(required = false) String newOrder, Model model) {
        var orderOpt = orderService.getOrderById(id);
        return orderOpt.map(order -> {
            model.addAttribute("order", OrderDto.from(order));
            return "order";
            }).orElse("notfound");
    }

    @PostMapping("/buy")
    public String buy() {
        var cartItems = cartService.getCartItems();
        var order = orderService.createOrder(cartItems);
        cartService.clear();

        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }
}
