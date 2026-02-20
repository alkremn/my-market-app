package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebFluxTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {

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
        User user = new User("newuser", "encoded");
        user.setId(1L);
        user.setEnabled(true);
        when(userService.registerUser(eq("newuser"), eq("Pass1!ab")))
                .thenReturn(Mono.just(user));
        when(userService.loginUser(any(User.class), any(ServerWebExchange.class)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/register")
                .bodyValue("username=newuser&password=Pass1!ab")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/items");

        verify(userService).registerUser(eq("newuser"), eq("Pass1!ab"));
        verify(userService).loginUser(any(User.class), any(ServerWebExchange.class));
    }

    @Test
    void register_shouldShowRegisterPage_whenRegistrationFails() {
        when(userService.registerUser(eq("existing"), eq("Pass1!ab")))
                .thenReturn(Mono.error(new RuntimeException("Имя пользователя уже занято")));

        webTestClient.post()
                .uri("/register")
                .bodyValue("username=existing&password=Pass1!ab")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .exchange()
                .expectStatus().isOk();

        verify(userService).registerUser(eq("existing"), eq("Pass1!ab"));
    }

    @Test
    void register_shouldShowRegisterPage_whenPasswordTooShort() {
        webTestClient.post()
                .uri("/register")
                .bodyValue("username=newuser&password=ab")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .exchange()
                .expectStatus().isOk();

        verify(userService, never()).registerUser(any(), any());
    }

    @Test
    void register_shouldShowRegisterPage_whenPasswordLacksComplexity() {
        webTestClient.post()
                .uri("/register")
                .bodyValue("username=newuser&password=simplepwd")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .exchange()
                .expectStatus().isOk();

        verify(userService, never()).registerUser(any(), any());
    }

    @Test
    void register_shouldShowRegisterPage_whenUsernameBlank() {
        webTestClient.post()
                .uri("/register")
                .bodyValue("username=&password=Pass1!ab")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .exchange()
                .expectStatus().isOk();

        verify(userService, never()).registerUser(any(), any());
    }
}
