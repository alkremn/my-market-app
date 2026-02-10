package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.Request.ItemsQueryRequest;
import co.kremnev.mymarket.dto.cache.CachedItem;
import co.kremnev.mymarket.dto.cache.CachedItemsPage;
import co.kremnev.mymarket.model.Item;
import co.kremnev.mymarket.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CacheService cacheService;

    private ItemServiceImpl itemService;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(itemRepository, cacheService);

        item1 = Item.builder().id(1L).title("Apple").price(BigDecimal.valueOf(10.0)).build();
        item2 = Item.builder().id(2L).title("Banana").price(BigDecimal.valueOf(20.0)).build();
    }

    @Test
    void getById_shouldReturnFromCache_whenCacheHit() {
        when(cacheService.get("item:1", Item.class)).thenReturn(Mono.just(item1));

        StepVerifier.create(itemService.getById(1L))
                .assertNext(item -> {
                    assertEquals(1L, item.getId());
                    assertEquals("Apple", item.getTitle());
                })
                .verifyComplete();

        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void getById_shouldFetchFromDbAndCache_whenCacheMiss() {
        when(cacheService.get("item:1", Item.class)).thenReturn(Mono.empty());
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(cacheService.set("item:1", item1)).thenReturn(Mono.just(true));

        StepVerifier.create(itemService.getById(1L))
                .assertNext(item -> {
                    assertEquals(1L, item.getId());
                    assertEquals("Apple", item.getTitle());
                })
                .verifyComplete();

        verify(itemRepository).findById(1L);
        verify(cacheService).set("item:1", item1);
    }

    @Test
    void getById_shouldReturnEmpty_whenNotFoundAnywhere() {
        when(cacheService.get("item:99", Item.class)).thenReturn(Mono.empty());
        when(itemRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(itemService.getById(99L))
                .verifyComplete();

        verify(cacheService, never()).set(anyString(), any());
    }

    @Test
    void getAllItems_shouldReturnFromCache_whenPageCacheHit() {
        ItemsQueryRequest request = new ItemsQueryRequest("", "NO", 1, 5);
        String listKey = "list:search::sort:NO:page:1:size:5";

        List<CachedItem> cachedItems = List.of(
                CachedItem.fromItem(item1),
                CachedItem.fromItem(item2)
        );
        CachedItemsPage cachedPage = new CachedItemsPage(cachedItems, 2);

        when(cacheService.get(listKey, CachedItemsPage.class)).thenReturn(Mono.just(cachedPage));
        when(cacheService.get("item:1", Item.class)).thenReturn(Mono.just(item1));
        when(cacheService.get("item:2", Item.class)).thenReturn(Mono.just(item2));

        StepVerifier.create(itemService.getAllItems(request))
                .assertNext(page -> {
                    assertEquals(2, page.getContent().size());
                    assertEquals(2, page.getTotalElements());
                    assertEquals("Apple", page.getContent().get(0).getTitle());
                    assertEquals("Banana", page.getContent().get(1).getTitle());
                })
                .verifyComplete();

        verify(itemRepository, never()).findAllBy(any(Pageable.class));
    }

    @Test
    void getAllItems_shouldResolveIndividualItemsFromDb_whenPageCachedButItemsNot() {
        ItemsQueryRequest request = new ItemsQueryRequest("", "NO", 1, 5);
        String listKey = "list:search::sort:NO:page:1:size:5";

        List<CachedItem> cachedItems = List.of(CachedItem.fromItem(item1));
        CachedItemsPage cachedPage = new CachedItemsPage(cachedItems, 1);

        // Page is cached, but individual item is not
        when(cacheService.get(listKey, CachedItemsPage.class)).thenReturn(Mono.just(cachedPage));
        when(cacheService.get("item:1", Item.class)).thenReturn(Mono.empty());
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item1));
        when(cacheService.set("item:1", item1)).thenReturn(Mono.just(true));

        StepVerifier.create(itemService.getAllItems(request))
                .assertNext(page -> {
                    assertEquals(1, page.getContent().size());
                    assertEquals("Apple", page.getContent().get(0).getTitle());
                })
                .verifyComplete();

        verify(itemRepository).findById(1L);
        verify(cacheService).set("item:1", item1);
    }

    @Test
    void getAllItems_shouldFetchFromDb_whenCacheMiss_noSearch() {
        ItemsQueryRequest request = new ItemsQueryRequest("", "NO", 1, 5);
        String listKey = "list:search::sort:NO:page:1:size:5";

        when(cacheService.get(listKey, CachedItemsPage.class)).thenReturn(Mono.empty());
        when(itemRepository.findAllBy(any(Pageable.class))).thenReturn(Flux.just(item1, item2));
        when(cacheService.set(eq("item:1"), eq(item1))).thenReturn(Mono.just(true));
        when(cacheService.set(eq("item:2"), eq(item2))).thenReturn(Mono.just(true));
        when(itemRepository.count()).thenReturn(Mono.just(2L));
        when(cacheService.set(eq(listKey), any(CachedItemsPage.class))).thenReturn(Mono.just(true));

        StepVerifier.create(itemService.getAllItems(request))
                .assertNext(page -> {
                    assertEquals(2, page.getContent().size());
                    assertEquals(2, page.getTotalElements());
                })
                .verifyComplete();

        verify(itemRepository).findAllBy(any(Pageable.class));
        verify(cacheService).set(eq(listKey), any(CachedItemsPage.class));
    }

    @Test
    void getAllItems_shouldUseSearchQuery_whenSearchProvided() {
        ItemsQueryRequest request = new ItemsQueryRequest("app", "NO", 1, 5);
        String listKey = "list:search:app:sort:NO:page:1:size:5";

        when(cacheService.get(listKey, CachedItemsPage.class)).thenReturn(Mono.empty());
        when(itemRepository.findByTitleContainingIgnoreCase(eq("app"), any(Pageable.class)))
                .thenReturn(Flux.just(item1));
        when(cacheService.set(eq("item:1"), eq(item1))).thenReturn(Mono.just(true));
        when(itemRepository.count()).thenReturn(Mono.just(2L));
        when(cacheService.set(eq(listKey), any(CachedItemsPage.class))).thenReturn(Mono.just(true));

        StepVerifier.create(itemService.getAllItems(request))
                .assertNext(page -> {
                    assertEquals(1, page.getContent().size());
                    assertEquals("Apple", page.getContent().get(0).getTitle());
                })
                .verifyComplete();

        verify(itemRepository).findByTitleContainingIgnoreCase(eq("app"), any(Pageable.class));
        verify(itemRepository, never()).findAllBy(any(Pageable.class));
    }

    @Test
    void getAllItems_shouldReturnEmptyPage_whenNoItemsInDb() {
        ItemsQueryRequest request = new ItemsQueryRequest("xyz", "NO", 1, 5);
        String listKey = "list:search:xyz:sort:NO:page:1:size:5";

        when(cacheService.get(listKey, CachedItemsPage.class)).thenReturn(Mono.empty());
        when(itemRepository.findByTitleContainingIgnoreCase(eq("xyz"), any(Pageable.class)))
                .thenReturn(Flux.empty());
        when(itemRepository.count()).thenReturn(Mono.just(0L));
        when(cacheService.set(eq(listKey), any(CachedItemsPage.class))).thenReturn(Mono.just(true));

        StepVerifier.create(itemService.getAllItems(request))
                .assertNext(page -> {
                    assertTrue(page.getContent().isEmpty());
                    assertEquals(0, page.getTotalElements());
                })
                .verifyComplete();
    }

    @Test
    void getAllItems_shouldSortByTitle_whenSortIsAlpha() {
        ItemsQueryRequest request = new ItemsQueryRequest("", "ALPHA", 1, 5);
        String listKey = "list:search::sort:ALPHA:page:1:size:5";

        when(cacheService.get(listKey, CachedItemsPage.class)).thenReturn(Mono.empty());
        when(itemRepository.findAllBy(any(Pageable.class))).thenReturn(Flux.just(item1));
        when(cacheService.set(eq("item:1"), eq(item1))).thenReturn(Mono.just(true));
        when(itemRepository.count()).thenReturn(Mono.just(1L));
        when(cacheService.set(eq(listKey), any(CachedItemsPage.class))).thenReturn(Mono.just(true));

        StepVerifier.create(itemService.getAllItems(request))
                .assertNext(page -> assertEquals(1, page.getContent().size()))
                .verifyComplete();

        verify(itemRepository).findAllBy(argThat(p ->
                p.getSort().getOrderFor("title") != null
                        && p.getSort().getOrderFor("title").isAscending()));
    }

    @Test
    void getAllItems_shouldSortByPrice_whenSortIsPrice() {
        ItemsQueryRequest request = new ItemsQueryRequest("", "PRICE", 1, 5);
        String listKey = "list:search::sort:PRICE:page:1:size:5";

        when(cacheService.get(listKey, CachedItemsPage.class)).thenReturn(Mono.empty());
        when(itemRepository.findAllBy(any(Pageable.class))).thenReturn(Flux.just(item2));
        when(cacheService.set(eq("item:2"), eq(item2))).thenReturn(Mono.just(true));
        when(itemRepository.count()).thenReturn(Mono.just(1L));
        when(cacheService.set(eq(listKey), any(CachedItemsPage.class))).thenReturn(Mono.just(true));

        StepVerifier.create(itemService.getAllItems(request))
                .assertNext(page -> assertEquals(1, page.getContent().size()))
                .verifyComplete();

        verify(itemRepository).findAllBy(argThat(p ->
                p.getSort().getOrderFor("price") != null
                        && p.getSort().getOrderFor("price").isAscending()));
    }

    @Test
    void getAllItems_shouldUseCorrectPageOffset() {
        ItemsQueryRequest request = new ItemsQueryRequest("", "NO", 3, 10);
        String listKey = "list:search::sort:NO:page:3:size:10";

        when(cacheService.get(listKey, CachedItemsPage.class)).thenReturn(Mono.empty());
        when(itemRepository.findAllBy(any(Pageable.class))).thenReturn(Flux.just(item1));
        when(cacheService.set(eq("item:1"), eq(item1))).thenReturn(Mono.just(true));
        when(itemRepository.count()).thenReturn(Mono.just(25L));
        when(cacheService.set(eq(listKey), any(CachedItemsPage.class))).thenReturn(Mono.just(true));

        StepVerifier.create(itemService.getAllItems(request))
                .assertNext(page -> {
                    assertEquals(2, page.getNumber()); // 0-based: request page 3 → index 2
                    assertEquals(10, page.getSize());
                })
                .verifyComplete();

        // Verify 1-based→0-based page conversion
        verify(itemRepository).findAllBy(argThat(p ->
                p.getPageNumber() == 2 && p.getPageSize() == 10));
    }
}
