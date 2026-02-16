package co.kremnev.mymarket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebFluxTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {

    @BeforeEach
    void setUp() {
        TestSecurityConfig.MOCK_CONTEXT.set(null);
    }

    @Test
    void loginPage_shouldReturnLoginView() {
        webTestClient.get()
                .uri("/login")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void loginPage_shouldPassErrorFlag_whenErrorParam() {
        webTestClient.get()
                .uri("/login?error")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void loginPage_shouldPassRegisteredFlag_whenRegisteredParam() {
        webTestClient.get()
                .uri("/login?registered")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void registerPage_shouldReturnRegisterView() {
        webTestClient.get()
                .uri("/register")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void register_shouldRedirectToItems_whenSuccessful() {
        when(userService.registerAndLogin(eq("newuser"), eq("password123"), any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/register")
                .bodyValue("username=newuser&password=password123")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/items");

        verify(userService).registerAndLogin(eq("newuser"), eq("password123"), any(ServerWebExchange.class));
    }

    @Test
    void register_shouldShowRegisterPage_whenRegistrationFails() {
        when(userService.registerAndLogin(eq("existing"), eq("password123"), any(ServerWebExchange.class)))
                .thenReturn(Mono.error(new RuntimeException("Имя пользователя уже занято")));

        webTestClient.post()
                .uri("/register")
                .bodyValue("username=existing&password=password123")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .exchange()
                .expectStatus().isOk();

        verify(userService).registerAndLogin(eq("existing"), eq("password123"), any(ServerWebExchange.class));
    }

    @Test
    void register_shouldShowRegisterPage_whenPasswordTooShort() {
        webTestClient.post()
                .uri("/register")
                .bodyValue("username=newuser&password=ab")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .exchange()
                .expectStatus().isOk();

        verify(userService, never()).registerAndLogin(any(), any(), any());
    }

    @Test
    void register_shouldShowRegisterPage_whenUsernameBlank() {
        webTestClient.post()
                .uri("/register")
                .bodyValue("username=&password=password123")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .exchange()
                .expectStatus().isOk();

        verify(userService, never()).registerAndLogin(any(), any(), any());
    }
}
