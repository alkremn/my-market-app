package co.kremnev.mymarket.dto;

import co.kremnev.mymarket.model.Item;

public record CartItem(Item item, int quantity) {
    public double getSubtotal() {
        return item.getPrice() * quantity;
    }
}
