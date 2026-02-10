package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
@Testcontainers
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        var now = LocalDateTime.now();

        item1 = Item.builder()
                .title("Laptop Computer")
                .description("High performance laptop")
                .price(BigDecimal.valueOf(999.99))
                .build();
        item1.setCreatedAt(now);
        item1.setUpdatedAt(now);

        item2 = Item.builder()
                .title("Desktop Computer")
                .description("Gaming desktop")
                .price(BigDecimal.valueOf(1499.99))
                .build();
        item2.setCreatedAt(now);
        item2.setUpdatedAt(now);

        item3 = Item.builder()
                .title("Wireless Mouse")
                .description("Ergonomic mouse")
                .price(BigDecimal.valueOf(29.99))
                .build();
        item3.setCreatedAt(now);
        item3.setUpdatedAt(now);

        // Clear and insert test data reactively
        itemRepository.deleteAll()
                .thenMany(itemRepository.saveAll(java.util.List.of(item1, item2, item3)))
                .collectList()
                .block();
    }

    @Test
    void findById_shouldReturnItem_whenExists() {
        StepVerifier.create(
                itemRepository.save(item1)
                        .flatMap(saved -> itemRepository.findById(saved.getId()))
        )
                .assertNext(found -> assertEquals("Laptop Computer", found.getTitle()))
                .verifyComplete();
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        StepVerifier.create(itemRepository.findById(999L))
                .verifyComplete();
    }

    @Test
    void findAll_shouldReturnAllItems() {
        StepVerifier.create(itemRepository.findAll().collectList())
                .assertNext(items -> assertEquals(3, items.size()))
                .verifyComplete();
    }

    @Test
    void save_shouldPersistNewItem() {
        var now = LocalDateTime.now();
        Item newItem = Item.builder()
                .title("Keyboard")
                .description("Mechanical keyboard")
                .price(BigDecimal.valueOf(89.99))
                .build();
        newItem.setCreatedAt(now);
        newItem.setUpdatedAt(now);

        StepVerifier.create(itemRepository.save(newItem))
                .assertNext(saved -> {
                    assertNotNull(saved.getId());
                    assertEquals("Keyboard", saved.getTitle());
                })
                .verifyComplete();

        StepVerifier.create(itemRepository.count())
                .assertNext(count -> assertEquals(4, count))
                .verifyComplete();
    }

    @Test
    void deleteById_shouldRemoveItem() {
        StepVerifier.create(
                itemRepository.save(item1)
                        .flatMap(saved -> itemRepository.deleteById(saved.getId())
                                .then(itemRepository.findById(saved.getId())))
        )
                .verifyComplete();
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldReturnMatchingItems_withExactMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        StepVerifier.create(itemRepository.findByTitleContainingIgnoreCase("Laptop", pageable).collectList())
                .assertNext(items -> {
                    assertEquals(1, items.size());
                    assertEquals("Laptop Computer", items.get(0).getTitle());
                })
                .verifyComplete();
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldReturnMatchingItems_withPartialMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        StepVerifier.create(itemRepository.findByTitleContainingIgnoreCase("computer", pageable).collectList())
                .assertNext(items -> {
                    assertEquals(2, items.size());
                    assertTrue(items.stream().anyMatch(item -> item.getTitle().equals("Laptop Computer")));
                    assertTrue(items.stream().anyMatch(item -> item.getTitle().equals("Desktop Computer")));
                })
                .verifyComplete();
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldBeCaseInsensitive() {
        Pageable pageable = PageRequest.of(0, 10);

        StepVerifier.create(itemRepository.findByTitleContainingIgnoreCase("laptop", pageable).collectList())
                .assertNext(items -> assertEquals(1, items.size()))
                .verifyComplete();

        StepVerifier.create(itemRepository.findByTitleContainingIgnoreCase("LAPTOP", pageable).collectList())
                .assertNext(items -> assertEquals(1, items.size()))
                .verifyComplete();

        StepVerifier.create(itemRepository.findByTitleContainingIgnoreCase("LaPtOp", pageable).collectList())
                .assertNext(items -> assertEquals(1, items.size()))
                .verifyComplete();
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldReturnEmpty_whenNoMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        StepVerifier.create(itemRepository.findByTitleContainingIgnoreCase("Nonexistent", pageable).collectList())
                .assertNext(items -> assertTrue(items.isEmpty()))
                .verifyComplete();
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldSupportPagination() {
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);

        StepVerifier.create(itemRepository.findByTitleContainingIgnoreCase("", firstPage).collectList())
                .assertNext(items -> assertEquals(2, items.size()))
                .verifyComplete();

        StepVerifier.create(itemRepository.findByTitleContainingIgnoreCase("", secondPage).collectList())
                .assertNext(items -> assertEquals(1, items.size()))
                .verifyComplete();
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldHandleEmptyString() {
        Pageable pageable = PageRequest.of(0, 10);

        StepVerifier.create(itemRepository.findByTitleContainingIgnoreCase("", pageable).collectList())
                .assertNext(items -> assertEquals(3, items.size()))
                .verifyComplete();
    }

    @Test
    void count_shouldReturnCorrectCount() {
        StepVerifier.create(itemRepository.count())
                .assertNext(count -> assertEquals(3, count))
                .verifyComplete();
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        StepVerifier.create(
                itemRepository.save(item1)
                        .flatMap(saved -> itemRepository.existsById(saved.getId()))
        )
                .assertNext(exists -> assertTrue(exists))
                .verifyComplete();
    }

    @Test
    void existsById_shouldReturnFalse_whenNotExists() {
        StepVerifier.create(itemRepository.existsById(999L))
                .assertNext(exists -> assertFalse(exists))
                .verifyComplete();
    }
}
