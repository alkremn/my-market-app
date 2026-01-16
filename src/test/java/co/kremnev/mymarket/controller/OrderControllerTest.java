package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.model.Item;
import co.kremnev.mymarket.model.Order;
import co.kremnev.mymarket.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Order Controller Integration Tests")
class OrderControllerTest extends BaseControllerTest {

    private MockHttpSession session;
    private Order testOrder;
    private Item testItem;

//    @BeforeEach
//    void setUp() {
//        session = new MockHttpSession();
//        testItem = Item.builder().id(1L).title("Test Item")
//                .description("Description").price(BigDecimal.valueOf(10.0)).build();
//        testOrder = createTestOrder(1L, List.of(
//            createTestOrderItem(1L, testItem, 2)
//        ));
//    }

//    @Test
//    void getOrders_shouldDisplayOrdersPage() throws Exception {
//        List<Order> orders = List.of(testOrder);
//        when(orderService.getAllOrders()).thenReturn(orders);
//
//        mockMvc.perform(get("/orders").session(session))
//            .andExpect(status().isOk())
//            .andExpect(view().name("orders"))
//            .andExpect(model().attributeExists("orders"));
//
//        verify(orderService).getAllOrders();
//    }
//
//    @Test
//    void getOrderById_shouldDisplayOrderPage_whenOrderExists() throws Exception {
//        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));
//
//        mockMvc.perform(get("/orders/1").session(session))
//            .andExpect(status().isOk())
//            .andExpect(view().name("order"))
//            .andExpect(model().attributeExists("order"));
//
//        verify(orderService).getOrderById(1L);
//    }
//
//    @Test
//    void getOrderById_shouldReturnNotFound_whenOrderDoesNotExist() throws Exception {
//        when(orderService.getOrderById(999L)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/orders/999").session(session))
//            .andExpect(status().isOk())
//            .andExpect(view().name("notfound"));
//
//        verify(orderService).getOrderById(999L);
//    }
//
//    @Test
//    void getOrderById_shouldAcceptNewOrderParameter() throws Exception {
//        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));
//
//        mockMvc.perform(get("/orders/1")
//                .param("newOrder", "true")
//                .session(session))
//            .andExpect(status().isOk())
//            .andExpect(view().name("order"));
//    }
//
//    @Test
//    void buy_shouldCreateOrderAndRedirect() throws Exception {
//        List<CartItem> cartItems = List.of(new CartItem(testItem, 2));
//        when(cartService.getCartItems()).thenReturn(cartItems);
//        when(orderService.createOrder(anyList())).thenReturn(testOrder);
//
//        mockMvc.perform(post("/buy").session(session))
//            .andExpect(status().is3xxRedirection())
//            .andExpect(redirectedUrl("/orders/1?newOrder=true"));
//
//        verify(cartService).getCartItems();
//        verify(orderService).createOrder(cartItems);
//    }
//
//    @Test
//    void buy_shouldHandleEmptyCart() throws Exception {
//        when(cartService.getCartItems()).thenReturn(List.of());
//        when(orderService.createOrder(anyList())).thenReturn(testOrder);
//
//        mockMvc.perform(post("/buy").session(session))
//            .andExpect(status().is3xxRedirection())
//            .andExpect(redirectedUrl("/orders/1?newOrder=true"));
//
//        verify(cartService).getCartItems();
//        verify(orderService).createOrder(List.of());
//    }
//
//    private OrderItem createTestOrderItem(long id, Item item, int quantity) {
//        try {
//            OrderItem orderItem = new OrderItem();
//            var idField = OrderItem.class.getDeclaredField("id");
//            idField.setAccessible(true);
//            idField.set(orderItem, id);
//
//            var itemField = OrderItem.class.getDeclaredField("item");
//            itemField.setAccessible(true);
//            itemField.set(orderItem, item);
//
//            var quantityField = OrderItem.class.getDeclaredField("quantity");
//            quantityField.setAccessible(true);
//            quantityField.set(orderItem, quantity);
//
//            return orderItem;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private Order createTestOrder(long id, List<OrderItem> orderItems) {
//        try {
//            Order order = new Order();
//            var idField = Order.class.getDeclaredField("id");
//            idField.setAccessible(true);
//            idField.set(order, id);
//
//            var orderItemsField = Order.class.getDeclaredField("orderItems");
//            orderItemsField.setAccessible(true);
//            orderItemsField.set(order, new ArrayList<>(orderItems));
//
//            var createdAtField = Order.class.getDeclaredField("createdAt");
//            createdAtField.setAccessible(true);
//            createdAtField.set(order, new Date());
//
//            return order;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}
