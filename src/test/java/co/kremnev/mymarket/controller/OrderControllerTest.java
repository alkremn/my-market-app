package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.model.Item;
import co.kremnev.mymarket.model.Order;
import co.kremnev.mymarket.service.CartService;
import co.kremnev.mymarket.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CartService cartService;

    private Order testOrder;
    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = Item.builder()
                .id(1L)
                .title("Test Item")
                .description("Description")
                .price(BigDecimal.valueOf(10.0))
                .build();

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
        List<CartItem> cartItems = List.of(new CartItem(testItem, 2));

        when(cartService.getCartItems(any(WebSession.class)))
                .thenReturn(Flux.fromIterable(cartItems));
        when(orderService.create(anyList())).thenReturn(Mono.just(testOrder));
        when(cartService.clear(any(WebSession.class))).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("orders/1?newOrder=true");

        verify(cartService).getCartItems(any(WebSession.class));
        verify(orderService).create(anyList());
        verify(cartService).clear(any(WebSession.class));
    }

    @Test
    void buy_shouldClearCartAfterCreatingOrder() {
        List<CartItem> cartItems = List.of(new CartItem(testItem, 1));

        when(cartService.getCartItems(any(WebSession.class)))
                .thenReturn(Flux.fromIterable(cartItems));
        when(orderService.create(anyList())).thenReturn(Mono.just(testOrder));
        when(cartService.clear(any(WebSession.class))).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();

        verify(cartService).clear(any(WebSession.class));
    }
}
