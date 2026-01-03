package co.kremnev.mymarket.dto.Request;

public record CartCommandRequest(
        Long id,
        String action,
        String search,
        String sort,
        Integer pageNumber,
        Integer pageSize
) {
    public CartCommandRequest(Long id, String action, String search, String sort, Integer pageNumber, Integer pageSize) {
        this.id = id;
        this.action = action;
        this.search = search != null ? search : "";
        this.sort = sort != null ? sort : "NO";
        this.pageNumber = pageNumber != null ? pageNumber : 1;
        this.pageSize = pageSize != null ? pageSize : 5;
    }
}
