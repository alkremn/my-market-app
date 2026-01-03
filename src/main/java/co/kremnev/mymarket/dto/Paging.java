package co.kremnev.mymarket.dto;

public record Paging(int pageNumber, int pageSize, boolean hasPrevious, boolean hasNext) {
}
