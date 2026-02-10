package co.kremnev.payment.service;

import co.kremnev.payment.model.Balance;
import co.kremnev.payment.model.PaymentRequest;
import co.kremnev.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentServiceImpl paymentService;

    private UUID testUserId;
    private Balance testBalance;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository);
        testUserId = UUID.randomUUID();
        testBalance = new Balance(testUserId, BigDecimal.valueOf(500.00));
    }

    @Test
    void getUserBalance_shouldReturnExistingBalance() {
        when(paymentRepository.findById(testUserId)).thenReturn(Mono.just(testBalance));

        StepVerifier.create(paymentService.getUserBalance(testUserId.toString()))
                .assertNext(balance -> {
                    assertEquals(testUserId, balance.getUserId());
                    assertEquals(BigDecimal.valueOf(500.00), balance.getBalance());
                })
                .verifyComplete();

        verify(paymentRepository).findById(testUserId);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void getUserBalance_shouldCreateNewBalance_whenUserNotFound() {
        when(paymentRepository.findById(testUserId)).thenReturn(Mono.empty());
        when(paymentRepository.save(any(Balance.class))).thenAnswer(invocation -> {
            Balance balance = invocation.getArgument(0);
            return Mono.just(balance);
        });

        StepVerifier.create(paymentService.getUserBalance(testUserId.toString()))
                .assertNext(balance -> {
                    assertEquals(testUserId, balance.getUserId());
                    assertNotNull(balance.getBalance());
                    assertTrue(balance.getBalance().compareTo(BigDecimal.valueOf(10000)) >= 0);
                    assertTrue(balance.getBalance().compareTo(BigDecimal.valueOf(100000)) <= 0);
                })
                .verifyComplete();

        verify(paymentRepository).save(any(Balance.class));
    }

    @Test
    void createPayment_shouldSucceed_whenSufficientBalance() {
        PaymentRequest request = new PaymentRequest()
                .userId(testUserId.toString())
                .amount(BigDecimal.valueOf(100.00));

        when(paymentRepository.findById(testUserId)).thenReturn(Mono.just(testBalance));
        when(paymentRepository.save(any(Balance.class))).thenAnswer(invocation -> {
            Balance balance = invocation.getArgument(0);
            return Mono.just(balance);
        });

        StepVerifier.create(paymentService.createPayment(request))
                .assertNext(response -> {
                    assertTrue(response.getSuccess());
                    assertEquals(BigDecimal.valueOf(400.00), response.getNewBalance());
                })
                .verifyComplete();

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(paymentRepository).save(balanceCaptor.capture());
        assertEquals(BigDecimal.valueOf(400.00), balanceCaptor.getValue().getBalance());
    }

    @Test
    void createPayment_shouldFail_whenInsufficientBalance() {
        PaymentRequest request = new PaymentRequest()
                .userId(testUserId.toString())
                .amount(BigDecimal.valueOf(600.00));

        when(paymentRepository.findById(testUserId)).thenReturn(Mono.just(testBalance));

        StepVerifier.create(paymentService.createPayment(request))
                .assertNext(response -> {
                    assertFalse(response.getSuccess());
                    assertEquals(BigDecimal.valueOf(500.00), response.getNewBalance());
                })
                .verifyComplete();

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_shouldFail_whenUserNotFound() {
        PaymentRequest request = new PaymentRequest()
                .userId(testUserId.toString())
                .amount(BigDecimal.valueOf(100.00));

        when(paymentRepository.findById(testUserId)).thenReturn(Mono.empty());

        StepVerifier.create(paymentService.createPayment(request))
                .assertNext(response -> {
                    assertFalse(response.getSuccess());
                })
                .verifyComplete();

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void createPayment_shouldSucceed_whenExactBalance() {
        PaymentRequest request = new PaymentRequest()
                .userId(testUserId.toString())
                .amount(BigDecimal.valueOf(500.00));

        when(paymentRepository.findById(testUserId)).thenReturn(Mono.just(testBalance));
        when(paymentRepository.save(any(Balance.class))).thenAnswer(invocation -> {
            Balance balance = invocation.getArgument(0);
            return Mono.just(balance);
        });

        StepVerifier.create(paymentService.createPayment(request))
                .assertNext(response -> {
                    assertTrue(response.getSuccess());
                    assertEquals(BigDecimal.valueOf(0.00).setScale(2), response.getNewBalance().setScale(2));
                })
                .verifyComplete();
    }
}
