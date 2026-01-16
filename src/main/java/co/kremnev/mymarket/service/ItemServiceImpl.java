package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.Request.ItemsQueryRequest;
import co.kremnev.mymarket.model.Item;
import co.kremnev.mymarket.repository.ItemRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Mono<Page<Item>> getAllItems(ItemsQueryRequest queryRequest) {
        Pageable pageable = createPageable(queryRequest);

        var search = queryRequest.getSearch();
        if (search.isEmpty()) {
            return getItemsPageableStream(itemRepository.findAllBy(pageable), pageable);
        }
        return getItemsPageableStream(itemRepository.findByTitleContainingIgnoreCase(search, pageable), pageable);
    }

    @Override
    public Mono<Item> getById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public Flux<Item> getByIds(Set<Long> ids) {
        return itemRepository.findAllById(ids);
    }

    private Pageable createPageable(ItemsQueryRequest queryRequest) {
        Sort sort = switch (queryRequest.getSort()) {
            case "ALPHA" -> Sort.by("title").ascending();
            case "PRICE" -> Sort.by("price").ascending();
            default -> Sort.unsorted();
        };

        return PageRequest.of(queryRequest.getPageNumber() - 1, queryRequest.getPageSize(), sort);
    }

    private Mono<Page<Item>> getItemsPageableStream(Flux<Item> itemsStream, Pageable pageable) {
        return itemsStream
                .collectList()
                .zipWith(itemRepository.count())
                .map(objects ->
                        new PageImpl<>(objects.getT1(), pageable, objects.getT2()));
    }
}
