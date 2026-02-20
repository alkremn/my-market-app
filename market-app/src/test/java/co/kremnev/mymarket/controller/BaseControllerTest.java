package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.model.SecurityUser;
import co.kremnev.mymarket.model.User;
import co.kremnev.mymarket.service.CartService;
import co.kremnev.mymarket.service.ItemService;
import co.kremnev.mymarket.service.OrderService;
import co.kremnev.mymarket.service.UserService;
import co.kremnev.payment.client.api.PaymentsApi;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@Import(TestSecurityConfig.class)
public abstract class BaseControllerTest {

    @Autowired
    protected WebTestClient webTestClient;

    @BeforeEach
    void clearSecurityContext() {
        TestSecurityConfig.MOCK_CONTEXT.set(null);
    }

    @MockitoBean
    protected CartService cartService;

    @MockitoBean
    protected OrderService orderService;

    @MockitoBean
    protected ItemService itemService;

    @MockitoBean
    protected PaymentsApi paymentsApi;

    @MockitoBean
    protected UserService userService;

    protected SecurityUser mockSecurityUser() {
        User user = new User("testuser", "password");
        user.setId(1L);
        user.setEnabled(true);
        return new SecurityUser(user);
    }

    protected UsernamePasswordAuthenticationToken createMockAuthentication() {
        SecurityUser user = mockSecurityUser();
        return UsernamePasswordAuthenticationToken.authenticated(
                user, user.getPassword(), user.getAuthorities());
    }

    protected WebTestClient authenticatedClient() {
        TestSecurityConfig.MOCK_CONTEXT.set(
                new SecurityContextImpl(createMockAuthentication()));
        return webTestClient;
    }
}
