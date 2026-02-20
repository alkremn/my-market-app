package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.Order;
import co.kremnev.mymarket.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Testcontainers
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll()
                .then(userRepository.deleteAll())
                .block();

        User user = new User("testuser", "password");
        user.setEnabled(true);
        testUserId = userRepository.save(user).map(User::getId).block();
    }

    private Order createOrder() {
        var now = LocalDateTime.now();
        Order order = new Order();
        order.setUserId(testUserId);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        return order;
    }

    @Test
    void save_shouldPersistOrder() {
        Order order = createOrder();

        StepVerifier.create(orderRepository.save(order))
                .assertNext(saved -> {
                    assertNotNull(saved.getId());
                    assertNotNull(saved.getCreatedAt());
                })
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnOrder_whenExists() {
        Order order = createOrder();

        StepVerifier.create(
                orderRepository.save(order)
                        .flatMap(saved -> orderRepository.findById(saved.getId()))
        )
                .assertNext(found -> assertNotNull(found.getId()))
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        StepVerifier.create(orderRepository.findById(999L))
                .verifyComplete();
    }

    @Test
    void findAll_shouldReturnAllOrders() {
        Order order1 = createOrder();
        Order order2 = createOrder();
        Order order3 = createOrder();

        orderRepository.saveAll(java.util.List.of(order1, order2, order3))
                .collectList()
                .block();

        StepVerifier.create(orderRepository.findAll().collectList())
                .assertNext(orders -> assertEquals(3, orders.size()))
                .verifyComplete();
    }

    @Test
    void count_shouldReturnCorrectCount() {
        Order order1 = createOrder();
        Order order2 = createOrder();

        orderRepository.saveAll(java.util.List.of(order1, order2))
                .collectList()
                .block();

        StepVerifier.create(orderRepository.count())
                .assertNext(count -> assertEquals(2, count))
                .verifyComplete();
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        Order order = createOrder();

        StepVerifier.create(
                orderRepository.save(order)
                        .flatMap(saved -> orderRepository.existsById(saved.getId()))
        )
                .assertNext(Assertions::assertTrue)
                .verifyComplete();
    }

    @Test
    void existsById_shouldReturnFalse_whenNotExists() {
        StepVerifier.create(orderRepository.existsById(999L))
                .assertNext(Assertions::assertFalse)
                .verifyComplete();
    }

    @Test
    void deleteById_shouldRemoveOrder() {
        Order order = createOrder();

        StepVerifier.create(
                orderRepository.save(order)
                        .flatMap(saved -> orderRepository.deleteById(saved.getId())
                                .then(orderRepository.findById(saved.getId())))
        )
                .verifyComplete();
    }

    @Test
    void deleteAll_shouldRemoveAllOrders() {
        Order order1 = createOrder();
        Order order2 = createOrder();

        orderRepository.saveAll(java.util.List.of(order1, order2))
                .collectList()
                .block();

        StepVerifier.create(
                orderRepository.deleteAll()
                        .then(orderRepository.count())
        )
                .assertNext(count -> assertEquals(0, count))
                .verifyComplete();
    }
}
