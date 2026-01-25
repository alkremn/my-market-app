package co.kremnev.mymarket.repository;

import co.kremnev.mymarket.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {
    Flux<Item> findAllBy(Pageable pageable);
    Flux<Item> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
