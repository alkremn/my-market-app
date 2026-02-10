package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.Request.ItemsQueryRequest;
import co.kremnev.mymarket.dto.cache.CachedItem;
import co.kremnev.mymarket.dto.cache.CachedItemsPage;
import co.kremnev.mymarket.model.Item;
import co.kremnev.mymarket.repository.ItemRepository;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private static final String ITEM_CACHE_KEY_PREFIX = "item:";
    private static final String LIST_CACHE_KEY_PREFIX = "list:";

    private final ItemRepository itemRepository;
    private final CacheService cacheService;

    public ItemServiceImpl(ItemRepository itemRepository, CacheService cacheService) {
        this.itemRepository = itemRepository;
        this.cacheService = cacheService;
    }

    @Override
    public Mono<Page<Item>> getAllItems(ItemsQueryRequest queryRequest) {
        String cacheKey = buildListCacheKey(queryRequest);
        Pageable pageable = createPageable(queryRequest);

        return cacheService.get(cacheKey, CachedItemsPage.class)
                .flatMap(cachedPage -> fetchFullItemsFromCache(cachedPage, pageable))
                .switchIfEmpty(fetchFromDatabase(queryRequest, pageable, cacheKey));
    }

    @Override
    public Mono<Item> getById(Long id) {
        String key = ITEM_CACHE_KEY_PREFIX + id;
        return cacheService.get(key, Item.class)
                .switchIfEmpty(
                        itemRepository.findById(id)
                                .flatMap(item -> cacheService.set(key, item)
                                        .thenReturn(item))
                );
    }

    private Pageable createPageable(ItemsQueryRequest queryRequest) {
        Sort sort = switch (queryRequest.getSort()) {
            case "ALPHA" -> Sort.by("title").ascending();
            case "PRICE" -> Sort.by("price").ascending();
            default -> Sort.unsorted();
        };

        return PageRequest.of(queryRequest.getPageNumber() - 1, queryRequest.getPageSize(), sort);
    }

    private String buildListCacheKey(ItemsQueryRequest request) {
        return LIST_CACHE_KEY_PREFIX +
                "search:" + request.getSearch().toLowerCase() +
                ":sort:" + request.getSort() +
                ":page:" + request.getPageNumber() +
                ":size:" + request.getPageSize();
    }

    private Mono<Page<Item>> fetchFullItemsFromCache(CachedItemsPage cachedItemsPage, Pageable pageable) {
        List<Long> ids = cachedItemsPage.getItems().stream()
                .map(CachedItem::getId)
                .toList();

        return Flux.fromIterable(ids)
                .flatMapSequential(this::getById)
                .collectList()
                .map(items -> new PageImpl<>(items, pageable, cachedItemsPage.getTotal()));
    }

    private Mono<Page<Item>> fetchFromDatabase(ItemsQueryRequest request, Pageable pageable,  String cacheKey) {
        Flux<Item> itemsFlux = request.getSearch().isEmpty()
                        ? itemRepository.findAllBy(pageable)
                        : itemRepository.findByTitleContainingIgnoreCase(request.getSearch(), pageable);

        return itemsFlux
                .flatMap(this::cacheItem)
                .collectList()
                .zipWith(itemRepository.count())
                .flatMap(tuple -> {
                    List<Item> items = tuple.getT1();
                    long total = tuple.getT2();

                    List<CachedItem> cachedItems = items.stream()
                            .map(CachedItem::fromItem)
                            .toList();
                    CachedItemsPage cachedItemsPage = new CachedItemsPage(cachedItems, total);
                    return cacheService.set(cacheKey, cachedItemsPage)
                            .thenReturn(new PageImpl<>(items, pageable, total));
                });
    }

    private Mono<Item> cacheItem(Item item) {
        String key = ITEM_CACHE_KEY_PREFIX + item.getId();
        return cacheService.set(key, item)
                .thenReturn(item);
    }
}
