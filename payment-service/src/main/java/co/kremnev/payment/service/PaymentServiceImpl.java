package co.kremnev.payment.service;

import co.kremnev.payment.model.Balance;
import co.kremnev.payment.model.PaymentRequest;
import co.kremnev.payment.model.PaymentResponse;
import co.kremnev.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private static final int MIN_AMOUNT = 10_000;
    private static final int MAX_AMOUNT = 100_000;

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Mono<Balance> getUserBalance(String userId) {
        UUID userUuid = UUID.fromString(userId);
        return paymentRepository
                .findById(userUuid)
                .switchIfEmpty(Mono.defer(() -> {
                    var newBalance = new BigDecimal(getRandomNumber());
                    return paymentRepository.save(new Balance(
                            userUuid,
                            newBalance.setScale(2, RoundingMode.HALF_EVEN)
                    ));
                }));
    }

    @Override
    public Mono<PaymentResponse> createPayment(PaymentRequest request) {
        UUID userUuid = UUID.fromString(request.getUserId());
        return paymentRepository.findById(userUuid)
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
                .switchIfEmpty(Mono.just(new PaymentResponse().success(false)));
    }

    private double getRandomNumber() {
        var randomNumber = ((Math.random() * (MAX_AMOUNT - MIN_AMOUNT)) + MIN_AMOUNT);
        return Math.round(randomNumber * 100.0) / 100.0;
    }
}
