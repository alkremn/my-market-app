package co.kremnev.mymarket.dto;

public final class CartCommandRequest {
    private final Long id;
    private final String action;
    private final String search;
    private final String sort;
    private final Integer pageNumber;
    private final Integer pageSize;

    public CartCommandRequest(Long id, String action, String search, String sort, Integer pageNumber, Integer pageSize) {
        this.id = id;
        this.action = action;
        this.search = search != null ? search : "";
        this.sort = sort != null ? sort : "NO";
        this.pageNumber = pageNumber != null ? pageNumber : 1;
        this.pageSize = pageSize != null ? pageSize : 5;
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public String getSearch() { return search; }
    public String getSort() { return sort; }
    public int getPageNumber() { return pageNumber; }
    public int getPageSize() { return pageSize; }
}
