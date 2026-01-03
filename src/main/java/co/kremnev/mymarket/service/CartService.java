package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.dto.SessionCart;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface CartService {
    SessionCart getCart(HttpSession session);
    void removeItem(HttpSession session, long itemId);
    void updateItemCount(HttpSession session, long itemId, int delta);
    List<CartItem> getCartItems(HttpSession session);
    double getCartTotal(List<CartItem> cartItems);
}
