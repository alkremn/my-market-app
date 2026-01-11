package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.dto.SessionCart;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    SessionCart getCart(HttpSession session);
    void removeItem(HttpSession session, long itemId);
    void updateItemCount(HttpSession session, long itemId, String action);
    List<CartItem> getCartItems(HttpSession session);
    BigDecimal getCartTotal(List<CartItem> cartItems);
}
