package co.kremnev.mymarket.dto;

import co.kremnev.mymarket.model.Item;

import java.math.BigDecimal;

public record CartItem(Item item, int quantity) {
    public BigDecimal getSubtotal() {
        return item.getPrice().multiply(new BigDecimal(quantity));
    }
}
