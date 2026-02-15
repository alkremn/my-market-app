package co.kremnev.mymarket.service;

import co.kremnev.mymarket.model.User;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface UserService extends ReactiveUserDetailsService, ReactiveUserDetailsPasswordService {
    Mono<User> registerUser(String username, String rawPassword);
    Mono<Void> registerAndLogin(String username, String rawPassword, ServerWebExchange exchange);
}
