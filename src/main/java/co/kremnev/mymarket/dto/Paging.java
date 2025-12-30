package co.kremnev.mymarket.dto;

public class Paging {
    private final int pageNumber;
    private final int pageSize;
    private final boolean hasPrevious;
    private final boolean hasNext;

    public Paging(int pageNumber, int pageSize, boolean hasPrevious, boolean hasNext) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
    }

    public int pageNumber() {
        return pageNumber;
    }
    public int pageSize() {
        return pageSize;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public boolean hasNext() {
        return hasNext;
    }
}
