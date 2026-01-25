package co.kremnev.mymarket.dto;

import co.kremnev.mymarket.model.OrderItem;

public record OrderItemDto(ItemDto item) {
    public static OrderItemDto from(OrderItem orderItem) {
        return new OrderItemDto(ItemDto.from(orderItem.getItem(), orderItem.getQuantity()));
    }
}
