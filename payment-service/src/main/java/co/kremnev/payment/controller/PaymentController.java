package co.kremnev.payment.controller;

import co.kremnev.payment.api.PaymentsApi;

import co.kremnev.payment.model.BalanceDto;
import co.kremnev.payment.model.CreateBalanceRequest;
import co.kremnev.payment.model.PaymentRequest;
import co.kremnev.payment.model.PaymentResponse;
import co.kremnev.payment.service.BalanceAlreadyExistsException;
import co.kremnev.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class PaymentController implements PaymentsApi {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public Mono<ResponseEntity<BalanceDto>> createBalance(Mono<CreateBalanceRequest> createBalanceRequest, ServerWebExchange exchange) {
        return createBalanceRequest
                .flatMap(req -> paymentService.createBalance(req.getUserId()))
                .map(balance -> new BalanceDto()
                        .userId(balance.getUserId()).balance(balance.getBalance()))
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .onErrorReturn(BalanceAlreadyExistsException.class,
                        ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @Override
    public Mono<ResponseEntity<BalanceDto>> getBalance(Long userId, ServerWebExchange exchange) {
        return paymentService.getUserBalance(userId)
                .map(balance -> new BalanceDto()
                        .userId(balance.getUserId()).balance(balance.getBalance()))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<PaymentResponse>> processPayment(Mono<PaymentRequest> paymentRequest, ServerWebExchange exchange) {
        return paymentRequest.flatMap(paymentService::createPayment)
                .map(response -> ResponseEntity.ok().body(response));
    }
}
