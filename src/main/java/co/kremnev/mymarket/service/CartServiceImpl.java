package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.CartItem;
import co.kremnev.mymarket.dto.SessionCart;
import co.kremnev.mymarket.model.Item;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private static final String CART_SESSION_KEY = "SHOPPING_CART";
    private final HttpSession session;
    private final ItemService itemService;

    public CartServiceImpl(HttpSession session, ItemService itemService) {
        this.session = session;
        this.itemService = itemService;
    }

    @Override
    public SessionCart getCart() {
        SessionCart cart = (SessionCart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new SessionCart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    @Override
    public void removeItem(long itemId) {
        SessionCart cart = getCart();
        cart.removeItem(itemId);
    }

    @Override
    public void updateItemCount(long itemId, String action) {
        SessionCart cart = getCart();
        if (action.equals("DELETE")) {
            cart.removeItem(itemId);
            return;
        }
        var itemCount = cart.getItemCountById(itemId);
        var newCount = itemCount + (action.equals("PLUS") ? 1 : -1);
        if (newCount < 0) {
            cart.removeItem(itemId);
        } else {
            cart.updateItem(itemId, newCount);
        }
    }

    @Override
    public List<CartItem> getCartItems() {
        SessionCart cart = getCart();
        if (cart.isEmpty()) {
            return new ArrayList<>();
        }

        List<Item> items = itemService.getByIds(cart.getItems().keySet());
        return items.stream()
            .map(item -> new CartItem(item, cart.getItems().get(item.getId())))
            .toList();
    }

    @Override
    public BigDecimal getCartTotal(List<CartItem> cartItems) {
        return cartItems.stream()
            .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void clear() {
        var cart = getCart();
        cart.clear();
    }
}
