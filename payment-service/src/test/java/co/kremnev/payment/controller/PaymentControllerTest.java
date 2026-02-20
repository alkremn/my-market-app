package co.kremnev.payment.controller;

import co.kremnev.payment.model.Balance;
import co.kremnev.payment.model.PaymentRequest;
import co.kremnev.payment.model.PaymentResponse;
import co.kremnev.payment.service.PaymentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(PaymentController.class)
@Import(TestPaymentSecurityConfig.class)
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PaymentService paymentService;

    private Long testUserId;
    private Balance testBalance;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testBalance = new Balance(testUserId, BigDecimal.valueOf(500.00));
        TestPaymentSecurityConfig.MOCK_CONTEXT.set(
                new SecurityContextImpl(mockJwtAuthentication()));
    }

    @AfterEach
    void tearDown() {
        TestPaymentSecurityConfig.MOCK_CONTEXT.set(null);
    }

    @Test
    void getBalance_shouldReturnBalance_whenAuthorized() {
        when(paymentService.getUserBalance(testUserId)).thenReturn(Mono.just(testBalance));

        webTestClient.get()
                .uri("/balance/{userId}", testUserId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userId").isEqualTo(testUserId)
                .jsonPath("$.balance").isEqualTo(500.00);

        verify(paymentService).getUserBalance(testUserId);
    }

    @Test
    void getBalance_shouldReturn401_whenNotAuthenticated() {
        TestPaymentSecurityConfig.MOCK_CONTEXT.set(null);

        webTestClient.get()
                .uri("/balance/{userId}", testUserId)
                .exchange()
                .expectStatus().isUnauthorized();

        verify(paymentService, never()).getUserBalance(any());
    }

    @Test
    void processPayment_shouldReturnSuccess_whenAuthorized() {
        PaymentResponse response = new PaymentResponse()
                .success(true)
                .newBalance(BigDecimal.valueOf(400.00));

        when(paymentService.createPayment(any(PaymentRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PaymentRequest()
                        .userId(testUserId)
                        .amount(BigDecimal.valueOf(100.00)))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.newBalance").isEqualTo(400.00);

        verify(paymentService).createPayment(any(PaymentRequest.class));
    }

    @Test
    void processPayment_shouldReturn401_whenNotAuthenticated() {
        TestPaymentSecurityConfig.MOCK_CONTEXT.set(null);

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PaymentRequest()
                        .userId(testUserId)
                        .amount(BigDecimal.valueOf(100.00)))
                .exchange()
                .expectStatus().isUnauthorized();

        verify(paymentService, never()).createPayment(any());
    }

    @Test
    void processPayment_shouldReturn403_whenNoAuthority() {
        // JWT present but without the required authority
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", "payment-client")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
        var noAuthorityToken = new JwtAuthenticationToken(jwt, List.of());
        TestPaymentSecurityConfig.MOCK_CONTEXT.set(new SecurityContextImpl(noAuthorityToken));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PaymentRequest()
                        .userId(testUserId)
                        .amount(BigDecimal.valueOf(100.00)))
                .exchange()
                .expectStatus().isForbidden();

        verify(paymentService, never()).createPayment(any());
    }

    @Test
    void processPayment_shouldReturnFailure_whenInsufficientBalance() {
        PaymentResponse response = new PaymentResponse()
                .success(false)
                .newBalance(BigDecimal.valueOf(500.00));

        when(paymentService.createPayment(any(PaymentRequest.class))).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PaymentRequest()
                        .userId(testUserId)
                        .amount(BigDecimal.valueOf(600.00)))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.newBalance").isEqualTo(500.00);
    }

    private JwtAuthenticationToken mockJwtAuthentication() {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", "payment-client")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();
        return new JwtAuthenticationToken(jwt,
                List.of(new SimpleGrantedAuthority("payment.balance.manage")));
    }
}
