package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.Request.RegisterRequest;
import co.kremnev.mymarket.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public Mono<Rendering> loginPage(ServerWebExchange exchange) {
        var params = exchange.getRequest().getQueryParams();
        return Mono.just(Rendering.view("login")
                .modelAttribute("hasError", params.containsKey("error"))
                .modelAttribute("hasRegistered", params.containsKey("registered"))
                .build());
    }

    @GetMapping("/register")
    public Mono<String> registerPage() {
        return Mono.just("register");
    }

    @PostMapping("/register")
    public Mono<String> register(@ModelAttribute @Valid RegisterRequest request,
                                 ServerWebExchange exchange, Model model) {
        return userService.registerAndLogin(request.username(), request.password(), exchange)
                .thenReturn("redirect:/items")
                .onErrorResume(e -> {
                    model.addAttribute("error", e.getMessage());
                    return Mono.just("register");
                });
    }
}
