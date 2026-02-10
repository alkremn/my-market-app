package co.kremnev.mymarket.dto.Request;

import co.kremnev.mymarket.model.CartAction;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartCommandRequest(
        @NotNull(message = "Item ID is required")
        @Min(value = 1, message = "Item ID must be positive")
        Long id,
        @NotNull(message = "Action is required")
        CartAction action,
        String search,
        String sort,
        Integer pageNumber,
        Integer pageSize
) {
    public CartCommandRequest(Long id, CartAction action, String search, String sort, Integer pageNumber, Integer pageSize) {
        this.id = id;
        this.action = action;
        this.search = search != null ? search : "";
        this.sort = sort != null ? sort : "NO";
        this.pageNumber = pageNumber != null ? pageNumber : 1;
        this.pageSize = pageSize != null ? pageSize : 5;
    }
}
