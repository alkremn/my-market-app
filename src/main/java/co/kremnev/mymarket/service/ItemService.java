package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.Request.ItemsQueryRequest;
import co.kremnev.mymarket.model.Item;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemService {
    Page<Item> getAllItems(ItemsQueryRequest queryRequest);
    Optional<Item> getById(long id);
    List<Item> getByIds(Set<Long> ids);
}
