package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.model.Order;
import co.kremnev.mymarket.model.OrderItem;
import co.kremnev.mymarket.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order createOrder(List<CartItem> cartItems) {
        var order = new Order();
        var orderItems = cartItems.stream().map(cartItem -> {
                    var orderItem = new OrderItem(cartItem.item(), cartItem.quantity());
                    orderItem.setOrder(order);
                    return orderItem;
                }).toList();
        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }
}
