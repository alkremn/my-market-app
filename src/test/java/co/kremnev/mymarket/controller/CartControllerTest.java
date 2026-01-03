package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Cart Controller Integration Tests")
class CartControllerTest extends BaseControllerTest {

    private MockHttpSession session;
    private Item testItem;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        testItem = createTestItem(1L, "Test Item", 10.0);
    }

    @Test
    void getCartItems_shouldDisplayCartPage() throws Exception {
        List<CartItem> cartItems = List.of(new CartItem(testItem, 2));
        when(cartService.getCartItems(any())).thenReturn(cartItems);
        when(cartService.getCartTotal(cartItems)).thenReturn(20.0);

        mockMvc.perform(get("/cart/items").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("cart"))
            .andExpect(model().attributeExists("items"))
            .andExpect(model().attributeExists("total"));

        verify(cartService).getCartItems(any());
        verify(cartService).getCartTotal(cartItems);
    }

    @Test
    void addOrRemoveToCart_shouldUpdateCart_whenActionIsPlus() throws Exception {
        mockMvc.perform(post("/items")
                .param("id", "1")
                .param("action", "PLUS")
                .param("search", "")
                .param("sort", "NO")
                .param("pageNumber", "1")
                .param("pageSize", "5")
                .session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/items?search=&sort=NO&pageNumber=1&pageSize=5"));

        verify(cartService).updateItemCount(any(), eq(1L), eq(1));
    }

    @Test
    void addOrRemoveToCart_shouldUpdateCart_whenActionIsMinus() throws Exception {
        mockMvc.perform(post("/items")
                .param("id", "1")
                .param("action", "MINUS")
                .param("search", "test")
                .param("sort", "PRICE")
                .param("pageNumber", "2")
                .param("pageSize", "10")
                .session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/items?search=test&sort=PRICE&pageNumber=2&pageSize=10"));

        verify(cartService).updateItemCount(any(), eq(1L), eq(-1));
    }

    @Test
    void addOrRemoveToCart_shouldRedirectToItemDetail_whenIdInPath() throws Exception {
        mockMvc.perform(post("/items/5")
                .param("id", "5")
                .param("action", "PLUS")
                .param("search", "")
                .param("sort", "NO")
                .param("pageNumber", "1")
                .param("pageSize", "5")
                .session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/items/5"));

        verify(cartService).updateItemCount(any(), eq(5L), eq(1));
    }

    @Test
    void updateCartFromCartPage_shouldUpdateAndReturnCartPage() throws Exception {
        List<CartItem> cartItems = List.of(new CartItem(testItem, 3));
        when(cartService.getCartItems(any())).thenReturn(cartItems);
        when(cartService.getCartTotal(cartItems)).thenReturn(30.0);

        mockMvc.perform(post("/cart/items")
                .param("id", "1")
                .param("action", "PLUS")
                .session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("cart"))
            .andExpect(model().attributeExists("items"))
            .andExpect(model().attributeExists("total"));

        verify(cartService).updateItemCount(any(), eq(1L), eq(1));
        verify(cartService).getCartItems(any());
    }

    @Test
    void addOrRemoveToCart_shouldNotUpdateCart_whenNoId() throws Exception {
        mockMvc.perform(post("/items")
                .param("action", "PLUS")
                .param("search", "")
                .param("sort", "NO")
                .param("pageNumber", "1")
                .param("pageSize", "5")
                .session(session))
            .andExpect(status().is3xxRedirection());

        verify(cartService, never()).updateItemCount(any(), anyLong(), anyInt());
    }

    @Test
    void addOrRemoveToCart_shouldNotUpdateCart_whenNoAction() throws Exception {
        mockMvc.perform(post("/items")
                .param("id", "1")
                .param("search", "")
                .param("sort", "NO")
                .param("pageNumber", "1")
                .param("pageSize", "5")
                .session(session))
            .andExpect(status().is3xxRedirection());

        verify(cartService, never()).updateItemCount(any(), anyLong(), anyInt());
    }

    private Item createTestItem(long id, String title, double price) {
        try {
            Item item = new Item();
            var idField = Item.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(item, id);

            var titleField = Item.class.getDeclaredField("title");
            titleField.setAccessible(true);
            titleField.set(item, title);

            var priceField = Item.class.getDeclaredField("price");
            priceField.setAccessible(true);
            priceField.set(item, price);

            return item;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
