package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.CartCommandRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CartController {

    @PostMapping({"/items", "/items/{id}"})
    public String addOrRemoveToCart(@PathVariable(required = false) String id, @ModelAttribute CartCommandRequest request) {




        return "redirect:/items";
    }

    @GetMapping("/cart/items")
    public String getItems(Model model) {

        return "cart";
    }
}


// POST /items?id=[id]&search=[search]&sort=[sort]&pageNumber=[pageNumber]&pageSize=[pageSize]&action=[action]
// POST /items/{id}?action=[action]