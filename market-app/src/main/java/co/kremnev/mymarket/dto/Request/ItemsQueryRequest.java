package co.kremnev.mymarket.dto.Request;

public final class ItemsQueryRequest {
    private final String search;
    private final String sort;
    private final Integer pageNumber;
    private final Integer pageSize;

    public ItemsQueryRequest(String search, String sort, Integer pageNumber, Integer pageSize) {
        this.search = search != null ? search : "";
        this.sort = sort != null ? sort : "NO";
        this.pageNumber = pageNumber != null ? pageNumber : 1;
        this.pageSize = pageSize != null ? pageSize : 5;
    }

    public String getSearch() { return search; }
    public String getSort() { return sort; }
    public int getPageNumber() { return pageNumber; }
    public int getPageSize() { return pageSize; }
}
