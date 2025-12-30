package co.kremnev.mymarket.service;

import co.kremnev.mymarket.dto.ItemsQueryRequest;
import co.kremnev.mymarket.model.Item;
import co.kremnev.mymarket.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Page<Item> getAllItems(ItemsQueryRequest queryRequest) {
        Pageable pageable = createPageable(queryRequest);

        var search = queryRequest.getSearch();
        if (search.isEmpty()) {
            return itemRepository.findAll(pageable);
        }
        return itemRepository.findByTitleContainingIgnoreCase(search, pageable);
    }

    @Override
    public Optional<Item> getById(long id) {
        return itemRepository.findById(id);
    }

    private Pageable createPageable(ItemsQueryRequest queryRequest) {
        Sort sort = switch (queryRequest.getSort()) {
            case "ALPHA" -> Sort.by("title").ascending();
            case "PRICE" -> Sort.by("price").ascending();
            default -> Sort.unsorted();
        };

        return PageRequest.of(queryRequest.getPageNumber() - 1, queryRequest.getPageSize(), sort);
    }
}
