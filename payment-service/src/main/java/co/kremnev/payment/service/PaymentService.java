package co.kremnev.payment.service;

import co.kremnev.payment.model.Balance;
import co.kremnev.payment.model.PaymentRequest;
import co.kremnev.payment.model.PaymentResponse;
import reactor.core.publisher.Mono;

public interface PaymentService {
    Mono<Balance> getUserBalance(String userId);
    Mono<PaymentResponse> createPayment(PaymentRequest request);
}
