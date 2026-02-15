package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.model.SecurityUser;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class SecurityModelAdvice {

    @ModelAttribute("principal")
    public Mono<SecurityUser> addPrincipal() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .filter(auth -> auth != null
                        && auth.isAuthenticated()
                        && !(auth instanceof AnonymousAuthenticationToken))
                .map(auth -> (SecurityUser) auth.getPrincipal())
                .switchIfEmpty(Mono.empty());
    }
}
