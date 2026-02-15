package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.Request.ItemsQueryRequest;
import co.kremnev.mymarket.model.Cart;
import co.kremnev.mymarket.model.CartItem;
import co.kremnev.mymarket.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@WebFluxTest(ItemController.class)
class ItemControllerTest extends BaseControllerTest {

    private Item testItem1;
    private Item testItem2;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        var now = LocalDateTime.now();

        testItem1 = Item.builder()
                .id(1L)
                .title("Test Item 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(10.0))
                .build();
        testItem1.setCreatedAt(now);
        testItem1.setUpdatedAt(now);

        testItem2 = Item.builder()
                .id(2L)
                .title("Test Item 2")
                .description("Description 2")
                .price(BigDecimal.valueOf(20.0))
                .build();
        testItem2.setCreatedAt(now);
        testItem2.setUpdatedAt(now);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUserId(1L);
        testCart.setItems(new ArrayList<>());
    }

    @Test
    void getItems_shouldDisplayItemsPage_anonymous() {
        TestSecurityConfig.MOCK_CONTEXT.set(null);

        Page<Item> page = new PageImpl<>(
                List.of(testItem1, testItem2),
                PageRequest.of(0, 5),
                2
        );

        when(itemService.getAllItems(any(ItemsQueryRequest.class))).thenReturn(Mono.just(page));

        webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk();

        verify(itemService).getAllItems(any(ItemsQueryRequest.class));
        verify(cartService, never()).getCart(anyLong());
    }

    @Test
    void getItems_shouldDisplayItemsPageAtRootUrl() {
        TestSecurityConfig.MOCK_CONTEXT.set(null);

        Page<Item> page = new PageImpl<>(
                List.of(testItem1),
                PageRequest.of(0, 5),
                1
        );

        when(itemService.getAllItems(any(ItemsQueryRequest.class))).thenReturn(Mono.just(page));

        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk();

        verify(itemService).getAllItems(any(ItemsQueryRequest.class));
    }

    @Test
    void getItems_shouldAcceptSearchParameter() {
        TestSecurityConfig.MOCK_CONTEXT.set(null);

        Page<Item> page = new PageImpl<>(
                List.of(testItem1),
                PageRequest.of(0, 5),
                1
        );

        when(itemService.getAllItems(any(ItemsQueryRequest.class))).thenReturn(Mono.just(page));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("search", "Test")
                        .build())
                .exchange()
                .expectStatus().isOk();

        verify(itemService).getAllItems(any(ItemsQueryRequest.class));
    }

    @Test
    void getItems_shouldAcceptSortParameter() {
        TestSecurityConfig.MOCK_CONTEXT.set(null);

        Page<Item> page = new PageImpl<>(
                List.of(testItem1, testItem2),
                PageRequest.of(0, 5),
                2
        );

        when(itemService.getAllItems(any(ItemsQueryRequest.class))).thenReturn(Mono.just(page));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("sort", "PRICE_ASC")
                        .build())
                .exchange()
                .expectStatus().isOk();

        verify(itemService).getAllItems(any(ItemsQueryRequest.class));
    }

    @Test
    void getItems_shouldAcceptPaginationParameters() {
        TestSecurityConfig.MOCK_CONTEXT.set(null);

        Page<Item> page = new PageImpl<>(
                List.of(testItem1),
                PageRequest.of(1, 10),
                11
        );

        when(itemService.getAllItems(any(ItemsQueryRequest.class))).thenReturn(Mono.just(page));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("pageNumber", "2")
                        .queryParam("pageSize", "10")
                        .build())
                .exchange()
                .expectStatus().isOk();

        verify(itemService).getAllItems(any(ItemsQueryRequest.class));
    }

    @Test
    void getItems_shouldDisplayEmptyPage() {
        TestSecurityConfig.MOCK_CONTEXT.set(null);

        Page<Item> page = new PageImpl<>(
                List.of(),
                PageRequest.of(0, 5),
                0
        );

        when(itemService.getAllItems(any(ItemsQueryRequest.class))).thenReturn(Mono.just(page));

        webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk();

        verify(itemService).getAllItems(any(ItemsQueryRequest.class));
    }

    @Test
    void getItem_shouldDisplayItemPage_whenItemExists() {
        TestSecurityConfig.MOCK_CONTEXT.set(null);

        when(itemService.getById(1L)).thenReturn(Mono.just(testItem1));

        webTestClient.get()
                .uri("/items/1")
                .exchange()
                .expectStatus().isOk();

        verify(itemService).getById(1L);
        verify(cartService, never()).getCart(anyLong());
    }

    @Test
    void getItem_shouldReturnNotFoundView_whenItemDoesNotExist() {
        TestSecurityConfig.MOCK_CONTEXT.set(null);

        when(itemService.getById(999L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/items/999")
                .exchange()
                .expectStatus().isOk();

        verify(itemService).getById(999L);
    }

    @Test
    void getItems_shouldLoadCart_whenAuthenticated() {
        Page<Item> page = new PageImpl<>(
                List.of(testItem1),
                PageRequest.of(0, 5),
                1
        );

        when(itemService.getAllItems(any(ItemsQueryRequest.class))).thenReturn(Mono.just(page));
        when(cartService.getCart(1L)).thenReturn(Mono.just(testCart));

        webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk();

        verify(cartService).getCart(1L);
    }

    @Test
    void getItem_shouldShowCartQuantity_whenAuthenticated() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setCartId(testCart.getId());
        cartItem.setItemId(1L);
        cartItem.setQuantity(3);
        testCart.setItems(List.of(cartItem));

        when(itemService.getById(1L)).thenReturn(Mono.just(testItem1));
        when(cartService.getCart(1L)).thenReturn(Mono.just(testCart));

        webTestClient.get()
                .uri("/items/1")
                .exchange()
                .expectStatus().isOk();

        verify(cartService).getCart(1L);
    }
}
