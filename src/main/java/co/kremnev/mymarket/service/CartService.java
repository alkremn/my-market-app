package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.dto.SessionCart;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    SessionCart getCart();
    void removeItem(long itemId);
    void updateItemCount(long itemId, String action);
    List<CartItem> getCartItems();
    BigDecimal getCartTotal(List<CartItem> cartItems);
    void clear();
}
