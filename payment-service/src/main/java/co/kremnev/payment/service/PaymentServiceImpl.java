package co.kremnev.payment.service;

import co.kremnev.payment.model.Balance;
import co.kremnev.payment.model.PaymentRequest;
import co.kremnev.payment.model.PaymentResponse;
import co.kremnev.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final int MIN_AMOUNT = 10_000;
    private static final int MAX_AMOUNT = 100_000;

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Mono<Balance> getUserBalance(Long userId) {
        return paymentRepository.findById(userId);
    }

    @Override
    public Mono<Balance> createBalance(Long userId) {
        return paymentRepository.findById(userId)
                .flatMap(existing -> Mono.<Balance>error(
                        new BalanceAlreadyExistsException(userId)))
                .switchIfEmpty(Mono.defer(() -> {
                    var amount = new BigDecimal(randomAmount())
                            .setScale(2, RoundingMode.HALF_EVEN);
                    return paymentRepository.save(new Balance(userId, amount));
                }));
    }

    @Override
    public Mono<PaymentResponse> createPayment(PaymentRequest request) {
        return paymentRepository.findById(request.getUserId())
                .flatMap(balance -> {
                    if (balance.getBalance().compareTo(request.getAmount()) < 0) {
                        return Mono.just(new PaymentResponse()
                                .success(false)
                                .newBalance(balance.getBalance()));
                    }
                    balance.setBalance(balance.getBalance().subtract(request.getAmount()));
                    return paymentRepository.save(balance)
                            .map(saved -> new PaymentResponse()
                                    .success(true)
                                    .newBalance(saved.getBalance()));
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just(new PaymentResponse().success(false))));
    }

    private double randomAmount() {
        double raw = (Math.random() * (MAX_AMOUNT - MIN_AMOUNT)) + MIN_AMOUNT;
        return Math.round(raw * 100.0) / 100.0;
    }
}
