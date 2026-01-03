package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.Item;
import co.kremnev.mymarket.model.Order;
import co.kremnev.mymarket.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("OrderRepository Tests")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        // Create test items
        testItem1 = createItem("Test Item 1", "Description 1", 10.0);
        testItem2 = createItem("Test Item 2", "Description 2", 20.0);

        entityManager.persist(testItem1);
        entityManager.persist(testItem2);
        entityManager.flush();
    }

    @Test
    void save_shouldPersistOrder() {
        Order order = new Order();

        Order saved = orderRepository.save(order);

        assertNotNull(saved.getId());
        assertTrue(orderRepository.findById(saved.getId()).isPresent());
    }

    @Test
    void save_shouldCascadeToOrderItems() {
        // Create order with items
        Order order = new Order();

        OrderItem orderItem1 = new OrderItem(testItem1, 2);
        orderItem1.setOrder(order);

        OrderItem orderItem2 = new OrderItem(testItem2, 3);
        orderItem2.setOrder(order);

        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem1);
        items.add(orderItem2);
        order.setOrderItems(items);

        // Save only the order (cascade should save order items)
        Order saved = orderRepository.save(order);

        // Verify order items were saved
        entityManager.flush();
        entityManager.clear();

        Order retrieved = orderRepository.findById(saved.getId()).orElseThrow();
        assertEquals(2, retrieved.getOrderItems().size());
    }

    @Test
    void delete_shouldCascadeToOrderItems() {
        // Create and save order with items
        Order order = new Order();

        OrderItem orderItem = new OrderItem(testItem1, 2);
        orderItem.setOrder(order);

        order.setOrderItems(List.of(orderItem));

        Order saved = orderRepository.save(order);
        Long orderId = saved.getId();

        entityManager.flush();
        entityManager.clear();

        // Delete the order
        orderRepository.deleteById(orderId);
        entityManager.flush();

        // Verify order and its items are deleted
        assertFalse(orderRepository.findById(orderId).isPresent());

        // Verify order items are deleted (due to cascade)
        List<?> remainingOrderItems = entityManager
            .createQuery("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
            .setParameter("orderId", orderId)
            .getResultList();

        assertTrue(remainingOrderItems.isEmpty());
    }

    @Test
    void findById_shouldReturnOrder_whenExists() {
        Order order = new Order();
        Order saved = orderRepository.save(order);

        Optional<Order> found = orderRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Order> found = orderRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findAll_shouldReturnAllOrders() {
        orderRepository.save(new Order());
        orderRepository.save(new Order());
        orderRepository.save(new Order());

        List<Order> orders = orderRepository.findAll();

        assertEquals(3, orders.size());
    }

    @Test
    void count_shouldReturnCorrectCount() {
        orderRepository.save(new Order());
        orderRepository.save(new Order());

        long count = orderRepository.count();

        assertEquals(2, count);
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        Order order = orderRepository.save(new Order());

        boolean exists = orderRepository.existsById(order.getId());

        assertTrue(exists);
    }

    @Test
    void existsById_shouldReturnFalse_whenNotExists() {
        boolean exists = orderRepository.existsById(999L);

        assertFalse(exists);
    }

    @Test
    void save_shouldMaintainBidirectionalRelationship() {
        Order order = new Order();

        OrderItem orderItem = new OrderItem(testItem1, 5);
        orderItem.setOrder(order);

        order.setOrderItems(List.of(orderItem));

        Order saved = orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        // Retrieve and verify relationship
        Order retrieved = orderRepository.findById(saved.getId()).orElseThrow();
        OrderItem retrievedItem = retrieved.getOrderItems().get(0);

        assertNotNull(retrievedItem.getOrder());
        assertEquals(retrieved.getId(), retrievedItem.getOrder().getId());
    }

    @Test
    void deleteAll_shouldRemoveAllOrders() {
        orderRepository.save(new Order());
        orderRepository.save(new Order());

        orderRepository.deleteAll();

        assertEquals(0, orderRepository.count());
    }

    private Item createItem(String title, String description, double price) {
        try {
            Item item = new Item();

            var titleField = Item.class.getDeclaredField("title");
            titleField.setAccessible(true);
            titleField.set(item, title);

            var descField = Item.class.getDeclaredField("description");
            descField.setAccessible(true);
            descField.set(item, description);

            var priceField = Item.class.getDeclaredField("price");
            priceField.setAccessible(true);
            priceField.set(item, price);

            return item;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test item", e);
        }
    }
}
