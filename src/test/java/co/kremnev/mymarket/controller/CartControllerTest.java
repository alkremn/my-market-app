package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebFluxTest(CartController.class)
class CartControllerTest extends BaseControllerTest {

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = Item.builder().id(1L).title("Test Item").price(BigDecimal.valueOf(10.0)).build();
    }

    @Test
    void getCartItems_shouldDisplayCartPage() throws Exception {
        Flux<CartItem> cartItems = Flux.just(new CartItem(testItem, 2));
        when(cartService.getCartItems(any(WebSession.class))).thenReturn(cartItems);
        when(cartService.getCartTotal(any(WebSession.class))).thenReturn(Mono.just(BigDecimal.valueOf(20.0)));

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk();

        verify(cartService).getCartItems(any(WebSession.class));
    }

    @Test
    void addOrRemoveToCart_shouldUpdateCart_whenActionIsPlus() throws Exception {
        when(cartService.updateItemCount(any(WebSession.class), eq(1L), eq("PLUS")))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("id", "1")
                        .queryParam("action", "PLUS")
                        .queryParam("search", "")
                        .queryParam("sort", "NO")
                        .queryParam("pageNumber", "1")
                        .queryParam("pageSize", "5")
                        .build()
                )
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/items?search=&sort=NO&pageNumber=1&pageSize=5");

        verify(cartService).updateItemCount(any(WebSession.class), eq(1L), eq("PLUS"));
    }

    @Test
    void addOrRemoveToCart_shouldUpdateCart_whenActionIsMinus() throws Exception {
        when(cartService.updateItemCount(any(WebSession.class), eq(1L), eq("MINUS")))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("id", "1")
                        .queryParam("action", "MINUS")
                        .queryParam("search", "test")
                        .queryParam("sort", "PRICE")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "10")
                        .build()
                )
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/items?search=test&sort=PRICE&pageNumber=2&pageSize=10");

        verify(cartService).updateItemCount(any(WebSession.class),eq(1L), eq("MINUS"));
    }

    @Test
    void addOrRemoveToCart_shouldRedirectToItemDetail_whenIdInPath() throws Exception {
        when(cartService.updateItemCount(any(WebSession.class), eq(5L), eq("PLUS")))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items/5")
                        .queryParam("id", "5")
                        .queryParam("action", "PLUS")
                        .queryParam("search", "")
                        .queryParam("sort", "NO")
                        .queryParam("pageNumber", "1")
                        .queryParam("pageSize", "5")
                        .build()
                )
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/items/5");

        verify(cartService).updateItemCount(any(WebSession.class),eq(5L), eq("PLUS"));
    }

    @Test
    void updateCartFromCartPage_shouldUpdateAndReturnCartPage() throws Exception {
        Flux<CartItem> cartItems = Flux.just(new CartItem(testItem, 3));

        when(cartService.updateItemCount(any(WebSession.class), eq(1L), eq("PLUS")))
                .thenReturn(Mono.empty());
        when(cartService.getCartItems(any(WebSession.class)))
                .thenReturn(cartItems);
        when(cartService.getCartTotal(any(WebSession.class)))
                .thenReturn(Mono.just(BigDecimal.valueOf(30.0)));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam("id", "1")
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().isOk();

        verify(cartService).updateItemCount(any(WebSession.class), eq(1L), eq("PLUS"));
        verify(cartService).getCartItems(any(WebSession.class));
    }

    @Test
    void addOrRemoveToCart_shouldNotUpdateCart_whenNoAction() throws Exception {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("id", "1")
                        .queryParam("search", "")
                        .queryParam("sort", "NO")
                        .queryParam("pageNumber", "1")
                        .queryParam("pageSize", "5")
                        .build()
                )
                .exchange()
                .expectStatus().is4xxClientError();

        verify(cartService, never()).updateItemCount(any(WebSession.class), anyLong(), eq("PLUS"));
    }
}
