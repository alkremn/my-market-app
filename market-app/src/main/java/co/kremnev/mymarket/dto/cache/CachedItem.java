package co.kremnev.mymarket.dto.cache;

import co.kremnev.mymarket.model.Item;

import java.math.BigDecimal;

public class CachedItem {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;

    public CachedItem() {}

    public CachedItem(Long id, String title, String description, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    public static CachedItem fromItem(Item item) {
        return new CachedItem(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
