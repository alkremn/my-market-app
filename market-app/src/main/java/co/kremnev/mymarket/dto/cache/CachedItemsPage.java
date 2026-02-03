package co.kremnev.mymarket.dto.cache;

import java.util.List;

public class CachedItemsPage {
    private List<CachedItem> items;
    private long total;

    public CachedItemsPage() {}

    public CachedItemsPage(List<CachedItem> items, long total) {
        this.items = items;
        this.total = total;
    }

    public List<CachedItem> getItems() {
        return items;
    }

    public void setItems(List<CachedItem> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
