package co.kremnev.mymarket.dto;

import co.kremnev.mymarket.model.Order;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
        long id,
        List<ItemDto> items,
        BigDecimal totalSum
) {
    public static OrderDto from(Order order) {
        var orderItems = order.getOrderItems();
        var totalSum = orderItems.stream()
                .map(x -> x.getItem().getPrice().multiply(new BigDecimal(x.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var items = orderItems.stream()
                .map(orderItem-> ItemDto.from(orderItem.getItem(), orderItem.getQuantity())).toList();
        return new OrderDto(order.getId(), items, totalSum);
    }
}
