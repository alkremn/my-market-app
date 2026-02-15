package co.kremnev.payment.controller;

import co.kremnev.payment.model.Balance;
import co.kremnev.payment.model.PaymentRequest;
import co.kremnev.payment.model.PaymentResponse;
import co.kremnev.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(PaymentController.class)
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
    }

    @Test
    void getBalance_shouldReturnBalance() {
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
    void processPayment_shouldReturnSuccess_whenPaymentSucceeds() {
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
}
