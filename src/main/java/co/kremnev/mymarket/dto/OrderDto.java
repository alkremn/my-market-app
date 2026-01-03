package co.kremnev.mymarket.dto;

import co.kremnev.mymarket.model.Order;
import java.util.List;

public record OrderDto(
        long id,
        List<ItemDto> items,
        double totalSum
) {
    public static OrderDto from(Order order) {
        var orderItems = order.getOrderItems();
        var totalSum = orderItems.stream().map(x -> x.getItem().getPrice() * x.getQuantity())
                .reduce(0.0, Double::sum);
        var items = orderItems.stream()
                .map(orderItem-> ItemDto.from(orderItem.getItem(), orderItem.getQuantity())).toList();
        return new OrderDto(order.getId(), items, totalSum);
    }
}
