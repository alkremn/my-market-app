package co.kremnev.mymarket.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

@TestConfiguration
@EnableWebFluxSecurity
public class TestSecurityConfig {

    /**
     * Static holder for test authentication. Set before each test to provide
     * a SecurityContext without reading from the Reactor context, which avoids
     * the StackOverflowError caused by ReactorContextTestExecutionListener's
     * SecuritySubContext wrapping in Spring Security 7.x.
     */
    static final AtomicReference<SecurityContext> MOCK_CONTEXT = new AtomicReference<>();

    @Bean
    public SecurityWebFilterChain testSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(new StaticSecurityContextRepository())
                .build();
    }

    private static class StaticSecurityContextRepository implements ServerSecurityContextRepository {
        @Override
        public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
            return Mono.empty();
        }

        @Override
        public Mono<SecurityContext> load(ServerWebExchange exchange) {
            SecurityContext ctx = MOCK_CONTEXT.get();
            return ctx != null ? Mono.just(ctx) : Mono.empty();
        }
    }
}
