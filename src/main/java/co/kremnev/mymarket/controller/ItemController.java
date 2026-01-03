package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.ItemDto;
import co.kremnev.mymarket.dto.Request.ItemsQueryRequest;
import co.kremnev.mymarket.dto.Paging;
import co.kremnev.mymarket.service.CartService;
import co.kremnev.mymarket.service.ItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;

    public ItemController(ItemService itemService, CartService cartService) {
        this.itemService = itemService;
        this.cartService = cartService;
    }

    @GetMapping(value = {"/", "/items"})
    public String getItems(@ModelAttribute ItemsQueryRequest queryParams, Model model, HttpSession session) {
        var page = itemService.getAllItems(queryParams);
        var cart = cartService.getCart(session);
        var itemsWithCount = page.getContent().stream()
                .map(item -> ItemDto.from(item, cart.getItemCountById(item.getId())));
        var currentPaging = new Paging(
                page.getNumber() + 1,
                page.getSize(),
                page.hasPrevious(),
                page.hasNext()
        );

        model.addAttribute("items", itemsWithCount);
        model.addAttribute("paging", currentPaging);
        model.addAttribute("search", queryParams.getSearch());
        model.addAttribute("sort", queryParams.getSort());
        return "items";
    }

    @GetMapping("/items/{id}")
    public String getItem(@PathVariable("id") long id, Model model, HttpSession session) {
        var itemOpt = itemService.getById(id);
        var cart = cartService.getCart(session);
        return itemOpt.map(item -> {
            model.addAttribute("item", ItemDto.from(item, cart.getItemCountById(item.getId())));
            return "item";
        }).orElse("notfound");
    }
}
