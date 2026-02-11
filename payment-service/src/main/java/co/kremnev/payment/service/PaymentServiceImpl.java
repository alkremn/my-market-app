package co.kremnev.payment.service;

import co.kremnev.payment.model.Balance;
import co.kremnev.payment.model.PaymentRequest;
import co.kremnev.payment.model.PaymentResponse;
import co.kremnev.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Mono<Balance> getUserBalance(Long userId) {
        return paymentRepository.findById(userId);
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
}
