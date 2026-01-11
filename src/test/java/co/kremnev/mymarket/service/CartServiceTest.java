package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.dto.SessionCart;
import co.kremnev.mymarket.model.Item;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    private static final String CART_SESSION_KEY = "SHOPPING_CART";

    @Mock
    private ItemService itemService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private CartServiceImpl cartService;

    private SessionCart sessionCart;
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    void setUp() {
        sessionCart = new SessionCart();

        testItem1 = Item.builder().id(1L).title("Item 1").price(BigDecimal.valueOf(10.0)).build();
        testItem2 = Item.builder().id(2L).title("Item 2").price(BigDecimal.valueOf(20.0)).build();
    }

    @Test
    void getCart_shouldCreateNewCart_whenCartDoesNotExist() {
        when(session.getAttribute(CART_SESSION_KEY)).thenReturn(null);

        SessionCart cart = cartService.getCart();

        assertNotNull(cart);
        assertTrue(cart.isEmpty());
        verify(session).setAttribute(eq(CART_SESSION_KEY), any(SessionCart.class));
    }

    @Test
    void getCart_shouldReturnExistingCart_whenCartExists() {
        when(session.getAttribute(CART_SESSION_KEY)).thenReturn(sessionCart);

        SessionCart cart = cartService.getCart();

        assertSame(sessionCart, cart);
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void updateItemCount_shouldAddItem_whenDeltaIsPositive() {
        when(session.getAttribute(CART_SESSION_KEY)).thenReturn(sessionCart);

        cartService.updateItemCount(1L, "PLUS");
        cartService.updateItemCount(1L, "PLUS");

        assertEquals(2, sessionCart.getItemCountById(1L));
    }

    @Test
    void updateItemCount_shouldIncreaseQuantity_whenItemAlreadyExists() {
        sessionCart.addItem(1L, 3);
        when(session.getAttribute(CART_SESSION_KEY)).thenReturn(sessionCart);

        cartService.updateItemCount(1L, "PLUS");
        cartService.updateItemCount(1L, "PLUS");

        assertEquals(5, sessionCart.getItemCountById(1L));
    }

    @Test
    void updateItemCount_shouldDecreaseQuantity_whenDeltaIsNegative() {
        sessionCart.addItem(1L, 5);
        when(session.getAttribute(CART_SESSION_KEY)).thenReturn(sessionCart);

        cartService.updateItemCount(1L, "MINUS");
        cartService.updateItemCount(1L, "MINUS");

        assertEquals(3, sessionCart.getItemCountById(1L));
    }

    @Test
    void updateItemCount_shouldRemoveItem_whenQuantityBecomesZero() {
        sessionCart.addItem(1L, 3);
        when(session.getAttribute(CART_SESSION_KEY)).thenReturn(sessionCart);

        cartService.updateItemCount(1L, "MINUS");
        cartService.updateItemCount(1L, "MINUS");
        cartService.updateItemCount(1L, "MINUS");

        assertEquals(0, sessionCart.getItemCountById(1L));
        assertTrue(sessionCart.isEmpty());
    }

    @Test
    void updateItemCount_shouldRemoveItem_whenQuantityBecomesNegative() {
        sessionCart.addItem(1L, 2);
        when(session.getAttribute(CART_SESSION_KEY)).thenReturn(sessionCart);

        cartService.updateItemCount(1L, "MINUS");
        cartService.updateItemCount(1L, "MINUS");

        assertEquals(0, sessionCart.getItemCountById(1L));
        assertTrue(sessionCart.isEmpty());
    }

    @Test
    void removeItem_shouldRemoveItemFromCart() {
        sessionCart.addItem(1L, 3);
        sessionCart.addItem(2L, 2);
        when(session.getAttribute(CART_SESSION_KEY)).thenReturn(sessionCart);

        cartService.removeItem(1L);

        assertEquals(0, sessionCart.getItemCountById(1L));
        assertEquals(2, sessionCart.getItemCountById(2L));
    }

    @Test
    void getCartItems_shouldReturnEmptyList_whenCartIsEmpty() {
        when(session.getAttribute(CART_SESSION_KEY)).thenReturn(sessionCart);

        List<CartItem> items = cartService.getCartItems();

        assertTrue(items.isEmpty());
        verify(itemService, never()).getByIds(any());
    }

    @Test
    void getCartItems_shouldReturnItemsWithQuantities() {
        sessionCart.addItem(1L, 2);
        sessionCart.addItem(2L, 3);
        when(session.getAttribute(CART_SESSION_KEY)).thenReturn(sessionCart);
        when(itemService.getByIds(Set.of(1L, 2L))).thenReturn(List.of(testItem1, testItem2));

        List<CartItem> items = cartService.getCartItems();

        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(ci -> ci.item().getId() == 1L && ci.quantity() == 2));
        assertTrue(items.stream().anyMatch(ci -> ci.item().getId() == 2L && ci.quantity() == 3));
    }

    @Test
    void getCartTotal_shouldCalculateCorrectTotal() {
        List<CartItem> cartItems = List.of(
            new CartItem(testItem1, 2), // 2 * 10.0 = 20.0
            new CartItem(testItem2, 3)  // 3 * 20.0 = 60.0
        );

        BigDecimal total = cartService.getCartTotal(cartItems);

        assertEquals(80.0, total.doubleValue(), 0.01);
    }

    @Test
    void getCartTotal_shouldReturnZero_whenCartIsEmpty() {
        BigDecimal total = cartService.getCartTotal(List.of());

        assertEquals(0.0, total.doubleValue(), 0.01);
    }
}
