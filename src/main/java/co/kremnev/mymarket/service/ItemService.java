package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.ItemsQueryRequest;
import co.kremnev.mymarket.model.Item;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ItemService {
    Page<Item> getAllItems(ItemsQueryRequest queryRequest);
    Optional<Item> getById(long id);
}
