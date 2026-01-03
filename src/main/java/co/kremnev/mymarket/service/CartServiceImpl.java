package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.dto.SessionCart;
import co.kremnev.mymarket.model.Item;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private static final String CART_SESSION_KEY = "SHOPPING_CART";
    private final ItemService itemService;

    public CartServiceImpl(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public SessionCart getCart(HttpSession session) {
        SessionCart cart = (SessionCart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new SessionCart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    @Override
    public void removeItem(HttpSession session, long itemId) {
        SessionCart cart = getCart(session);
        cart.removeItem(itemId);
    }

    @Override
    public void updateItemCount(HttpSession session, long itemId, int delta) {
        SessionCart cart = getCart(session);
        var itemCount = cart.getItemCountById(itemId);
        var newCount = itemCount + delta;
        if (newCount < 0) {
            cart.removeItem(itemId);
        } else {
            cart.updateItem(itemId, newCount);
        }
    }

    @Override
    public List<CartItem> getCartItems(HttpSession session) {
        SessionCart cart = getCart(session);
        if (cart.isEmpty()) {
            return new ArrayList<>();
        }

        List<Item> items = itemService.getByIds(cart.getItems().keySet());
        return items.stream()
            .map(item -> new CartItem(item, cart.getItems().get(item.getId())))
            .toList();
    }

    @Override
    public double getCartTotal(List<CartItem> cartItems) {
        return cartItems.stream()
            .mapToDouble(CartItem::getSubtotal)
                .sum();
    }
}
