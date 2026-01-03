package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("ItemRepository Tests")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        itemRepository.deleteAll();

        // Create test items
        item1 = createItem("Laptop Computer", "High performance laptop", 999.99);
        item2 = createItem("Desktop Computer", "Gaming desktop", 1499.99);
        item3 = createItem("Wireless Mouse", "Ergonomic mouse", 29.99);

        // Persist and flush to ensure they're in the database
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.flush();
    }

    @Test
    void findById_shouldReturnItem_whenExists() {
        Optional<Item> found = itemRepository.findById(item1.getId());

        assertTrue(found.isPresent());
        assertEquals("Laptop Computer", found.get().getTitle());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<Item> found = itemRepository.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findAll_shouldReturnAllItems() {
        var items = itemRepository.findAll();

        assertEquals(3, items.size());
    }

    @Test
    void save_shouldPersistNewItem() {
        Item newItem = createItem("Keyboard", "Mechanical keyboard", 89.99);

        Item saved = itemRepository.save(newItem);

        assertNotNull(saved.getId());
        assertEquals("Keyboard", saved.getTitle());
        assertEquals(4, itemRepository.count());
    }

    @Test
    void delete_shouldRemoveItem() {
        itemRepository.deleteById(item1.getId());

        assertEquals(2, itemRepository.count());
        assertFalse(itemRepository.findById(item1.getId()).isPresent());
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldReturnMatchingItems_withExactMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByTitleContainingIgnoreCase("Laptop", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop Computer", result.getContent().get(0).getTitle());
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldReturnMatchingItems_withPartialMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByTitleContainingIgnoreCase("computer", pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
            .anyMatch(item -> item.getTitle().equals("Laptop Computer")));
        assertTrue(result.getContent().stream()
            .anyMatch(item -> item.getTitle().equals("Desktop Computer")));
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldBeCaseInsensitive() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> resultLower = itemRepository.findByTitleContainingIgnoreCase("laptop", pageable);
        Page<Item> resultUpper = itemRepository.findByTitleContainingIgnoreCase("LAPTOP", pageable);
        Page<Item> resultMixed = itemRepository.findByTitleContainingIgnoreCase("LaPtOp", pageable);

        assertEquals(1, resultLower.getTotalElements());
        assertEquals(1, resultUpper.getTotalElements());
        assertEquals(1, resultMixed.getTotalElements());
        assertEquals(resultLower.getContent().get(0).getId(),
                     resultUpper.getContent().get(0).getId());
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldReturnEmpty_whenNoMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByTitleContainingIgnoreCase("Nonexistent", pageable);

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldSupportPagination() {
        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);

        Page<Item> page1 = itemRepository.findByTitleContainingIgnoreCase("", firstPage);
        Page<Item> page2 = itemRepository.findByTitleContainingIgnoreCase("", secondPage);

        assertEquals(2, page1.getContent().size());
        assertEquals(1, page2.getContent().size());
        assertEquals(3, page1.getTotalElements());
        assertTrue(page1.hasNext());
        assertFalse(page2.hasNext());
    }

    @Test
    void findByTitleContainingIgnoreCase_shouldHandleEmptyString() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> result = itemRepository.findByTitleContainingIgnoreCase("", pageable);

        assertEquals(3, result.getTotalElements());
    }

    @Test
    void count_shouldReturnCorrectCount() {
        long count = itemRepository.count();

        assertEquals(3, count);
    }

    @Test
    void existsById_shouldReturnTrue_whenExists() {
        boolean exists = itemRepository.existsById(item1.getId());

        assertTrue(exists);
    }

    @Test
    void existsById_shouldReturnFalse_whenNotExists() {
        boolean exists = itemRepository.existsById(999L);

        assertFalse(exists);
    }

    private Item createItem(String title, String description, double price) {
        try {
            Item item = new Item();

            var titleField = Item.class.getDeclaredField("title");
            titleField.setAccessible(true);
            titleField.set(item, title);

            var descField = Item.class.getDeclaredField("description");
            descField.setAccessible(true);
            descField.set(item, description);

            var priceField = Item.class.getDeclaredField("price");
            priceField.setAccessible(true);
            priceField.set(item, price);

            return item;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test item", e);
        }
    }
}
