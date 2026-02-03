package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.service.CartService;
import co.kremnev.mymarket.service.ItemService;
import co.kremnev.mymarket.service.OrderService;
import co.kremnev.payment.client.api.PaymentsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
public abstract class BaseControllerTest {

    @Autowired
    protected WebTestClient webTestClient;

    @MockitoBean
    protected CartService cartService;

    @MockitoBean
    protected OrderService orderService;

    @MockitoBean
    protected ItemService itemService;

    @MockitoBean
    protected PaymentsApi paymentsApi;
}
