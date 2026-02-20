package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.model.CartAction;
import co.kremnev.mymarket.model.CartItem;
import co.kremnev.mymarket.model.Item;
import co.kremnev.payment.client.model.BalanceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebFluxTest(CartController.class)
class CartControllerTest extends BaseControllerTest {

    private Item testItem;
    private CartItem testCartItem;
    private BalanceDto testBalance;

    @BeforeEach
    void setUp() {
        testItem = Item.builder().id(1L).title("Test Item").price(BigDecimal.valueOf(10.0)).build();

        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setCartId(1L);
        testCartItem.setItemId(1L);
        testCartItem.setQuantity(2);
        testCartItem.setItem(testItem);

        testBalance = new BalanceDto()
                .userId(1L)
                .balance(BigDecimal.valueOf(100.0));
    }

    @Test
    void getCartItems_shouldDisplayCartPage() {
        Flux<CartItem> cartItems = Flux.just(testCartItem);
        when(cartService.getCartItems(anyLong())).thenReturn(cartItems);
        when(cartService.getCartTotal(anyLong())).thenReturn(Mono.just(BigDecimal.valueOf(20.0)));
        when(paymentsApi.getBalance(anyLong())).thenReturn(Mono.just(testBalance));

        authenticatedClient().get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk();

        verify(cartService).getCartItems(anyLong());
    }

    @Test
    void addOrRemoveToCart_shouldUpdateCart_whenActionIsPlus() {
        when(cartService.updateItemCount(anyLong(), eq(1L), eq(CartAction.PLUS)))
                .thenReturn(Mono.empty());

        authenticatedClient().post()
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

        verify(cartService).updateItemCount(anyLong(), eq(1L), eq(CartAction.PLUS));
    }

    @Test
    void addOrRemoveToCart_shouldUpdateCart_whenActionIsMinus() {
        when(cartService.updateItemCount(anyLong(), eq(1L), eq(CartAction.MINUS)))
                .thenReturn(Mono.empty());

        authenticatedClient().post()
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

        verify(cartService).updateItemCount(anyLong(), eq(1L), eq(CartAction.MINUS));
    }

    @Test
    void addOrRemoveToCart_shouldRedirectToItemDetail_whenIdInPath() {
        when(cartService.updateItemCount(anyLong(), eq(5L), eq(CartAction.PLUS)))
                .thenReturn(Mono.empty());

        authenticatedClient().post()
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

        verify(cartService).updateItemCount(anyLong(), eq(5L), eq(CartAction.PLUS));
    }

    @Test
    void updateCartFromCartPage_shouldUpdateAndReturnCartPage() {
        Flux<CartItem> cartItems = Flux.just(testCartItem);

        when(cartService.updateItemCount(anyLong(), eq(1L), eq(CartAction.PLUS)))
                .thenReturn(Mono.empty());
        when(cartService.getCartItems(anyLong()))
                .thenReturn(cartItems);
        when(cartService.getCartTotal(anyLong()))
                .thenReturn(Mono.just(BigDecimal.valueOf(30.0)));
        when(paymentsApi.getBalance(anyLong())).thenReturn(Mono.just(testBalance));

        authenticatedClient().post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cart/items")
                        .queryParam("id", "1")
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().isOk();

        verify(cartService).updateItemCount(anyLong(), eq(1L), eq(CartAction.PLUS));
        verify(cartService).getCartItems(anyLong());
    }

    @Test
    void addOrRemoveToCart_shouldNotUpdateCart_whenNoAction() {
        authenticatedClient().post()
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

        verify(cartService, never()).updateItemCount(anyLong(), anyLong(), eq(CartAction.PLUS));
    }
}
