package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.model.CartItem;
import co.kremnev.mymarket.model.Item;
import co.kremnev.mymarket.model.Order;
import co.kremnev.mymarket.service.CartService;
import co.kremnev.mymarket.service.OrderService;
import co.kremnev.payment.client.api.PaymentsApi;
import co.kremnev.payment.client.model.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private PaymentsApi paymentsApi;

    private Order testOrder;
    private Item testItem;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testItem = Item.builder()
                .id(1L)
                .title("Test Item")
                .description("Description")
                .price(BigDecimal.valueOf(10.0))
                .build();

        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setCartId(1L);
        testCartItem.setItemId(1L);
        testCartItem.setQuantity(2);
        testCartItem.setItem(testItem);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setOrderItems(List.of());
    }

    @Test
    void getOrders_shouldDisplayOrdersPage() {
        when(orderService.getAll()).thenReturn(Flux.just(testOrder));

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk();

        verify(orderService).getAll();
    }

    @Test
    void getOrderById_shouldDisplayOrderPage_whenOrderExists() {
        when(orderService.getById(1L)).thenReturn(Mono.just(testOrder));

        webTestClient.get()
                .uri("/orders/1")
                .exchange()
                .expectStatus().isOk();

        verify(orderService).getById(1L);
    }

    @Test
    void getOrderById_shouldReturnNotFoundView_whenOrderDoesNotExist() {
        when(orderService.getById(999L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/orders/999")
                .exchange()
                .expectStatus().isOk();  // Returns 200 with "notfound" view

        verify(orderService).getById(999L);
    }

    @Test
    void getOrderById_shouldAcceptNewOrderParameter() {
        when(orderService.getById(1L)).thenReturn(Mono.just(testOrder));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/orders/1")
                        .queryParam("newOrder", "true")
                        .build())
                .exchange()
                .expectStatus().isOk();

        verify(orderService).getById(1L);
    }

    @Test
    void buy_shouldCreateOrderAndRedirect() {
        when(cartService.getCartItems(anyString()))
                .thenReturn(Flux.just(testCartItem));
        when(cartService.getCartTotal(anyString()))
                .thenReturn(Mono.just(BigDecimal.valueOf(20.0)));
        when(paymentsApi.processPayment(any()))
                .thenReturn(Mono.just(new PaymentResponse()));
        when(orderService.create(anyList())).thenReturn(Mono.just(testOrder));
        when(cartService.clear(anyString())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("orders/1?newOrder=true");

        verify(cartService).getCartItems(anyString());
        verify(orderService).create(anyList());
        verify(cartService).clear(anyString());
    }

    @Test
    void buy_shouldClearCartAfterCreatingOrder() {
        when(cartService.getCartItems(anyString()))
                .thenReturn(Flux.just(testCartItem));
        when(cartService.getCartTotal(anyString()))
                .thenReturn(Mono.just(BigDecimal.valueOf(10.0)));
        when(paymentsApi.processPayment(any()))
                .thenReturn(Mono.just(new PaymentResponse()));
        when(orderService.create(anyList())).thenReturn(Mono.just(testOrder));
        when(cartService.clear(anyString())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(cartService).clear(anyString());
    }
}
