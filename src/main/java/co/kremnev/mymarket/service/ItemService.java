package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.Request.ItemsQueryRequest;
import co.kremnev.mymarket.model.Item;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface ItemService {
    Mono<Page<Item>> getAllItems(ItemsQueryRequest queryRequest);
    Mono<Item> getById(Long id);
    Flux<Item> getByIds(Set<Long> ids);
}
