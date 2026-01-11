package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.model.Item;
import co.kremnev.mymarket.model.Order;
import co.kremnev.mymarket.model.OrderItem;
import co.kremnev.mymarket.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Item testItem1;
    private Item testItem2;
    private List<CartItem> cartItems;

    @BeforeEach
    void setUp() {
        testItem1 = Item.builder().id(1L).title("Item 1").description("Description 1").price(BigDecimal.valueOf(10.0)).build();
        testItem2 = Item.builder().id(2L).title("Item 2").description("Description 2").price(BigDecimal.valueOf(20.0)).build();

        cartItems = List.of(
            new CartItem(testItem1, 2),
            new CartItem(testItem2, 3)
        );
    }

    @Test
    void getAllOrders_shouldReturnAllOrders() {
        List<Order> expectedOrders = List.of(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        assertSame(expectedOrders, result);
        verify(orderRepository).findAll();
    }

    @Test
    void getOrderById_shouldReturnOrder_whenExists() {
        Order expectedOrder = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(expectedOrder));

        Optional<Order> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertSame(expectedOrder, result.get());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrderById_shouldReturnEmpty_whenNotExists() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrderById(999L);

        assertFalse(result.isPresent());
        verify(orderRepository).findById(999L);
    }

    @Test
    void createOrder_shouldCreateOrderWithItems() {
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(cartItems);

        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertNotNull(savedOrder);
        assertNotNull(savedOrder.getOrderItems());
        assertEquals(2, savedOrder.getOrderItems().size());
    }

    @Test
    void createOrder_shouldSetCorrectQuantitiesAndItems() {
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(cartItems);

        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        List<OrderItem> orderItems = savedOrder.getOrderItems();

        OrderItem orderItem1 = orderItems.stream()
            .filter(oi -> oi.getItem().getId() == 1L)
            .findFirst()
            .orElseThrow();
        assertEquals(2, orderItem1.getQuantity());
        assertEquals(testItem1, orderItem1.getItem());

        OrderItem orderItem2 = orderItems.stream()
            .filter(oi -> oi.getItem().getId() == 2L)
            .findFirst()
            .orElseThrow();
        assertEquals(3, orderItem2.getQuantity());
        assertEquals(testItem2, orderItem2.getItem());
    }

    @Test
    void createOrder_shouldSetBidirectionalRelationship() {
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrder(cartItems);

        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        for (OrderItem orderItem : savedOrder.getOrderItems()) {
            assertNotNull(orderItem.getOrder());
            assertSame(savedOrder, orderItem.getOrder());
        }
    }

    @Test
    void createOrder_shouldHandleEmptyCart() {
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(List.of());

        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertNotNull(savedOrder);
        assertNotNull(savedOrder.getOrderItems());
        assertTrue(savedOrder.getOrderItems().isEmpty());
    }
}
