package co.kremnev.mymarket.controller;

import co.kremnev.mymarket.dto.ItemsQueryRequest;
import co.kremnev.mymarket.dto.Paging;
import co.kremnev.mymarket.model.Item;
import co.kremnev.mymarket.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;

@Controller
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/items")
    public String getItems(@ModelAttribute ItemsQueryRequest queryParams, Model model) {
        var page = itemService.getAllItems(queryParams);
        var items = page.getContent();
        var result = new ArrayList<ArrayList<Item>>();
        var row = new ArrayList<Item>();

        var totalCount = items.size() + (items.size() - 1) % 3;
        for(var idx = 1; idx <= totalCount; idx += 1) {
            row.add(idx < items.size() - 1 ? items.get(idx) : new Item());
            if (row.size() == 3){
                result.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()){
            result.add(row);
        }
        model.addAttribute("items", result);
        model.addAttribute("paging", new Paging(
                page.getNumber() + 1,
                page.getSize(),
                page.hasPrevious(),
                page.hasNext()
        ));
        model.addAttribute("search", queryParams.getSearch());
        model.addAttribute("sort", queryParams.getSort());
        return "items";
    }

    @GetMapping("/items/{id}")
    public String getItem(@PathVariable("id") long id, Model model) {
        var itemOpt = itemService.getById(id);
        return itemOpt.map(item -> {
            model.addAttribute("item", item);
            return "item";
        }).orElse("notfound");
    }
}
