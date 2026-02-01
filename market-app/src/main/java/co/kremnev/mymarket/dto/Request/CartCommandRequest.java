package co.kremnev.mymarket.dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CartCommandRequest(
        @NotNull(message = "Item ID is required")
        @Min(value = 1, message = "Item ID must be positive")
        Long id,
        @NotEmpty(message = "Action is required")
        @Pattern(regexp = "^(PLUS|MINUS|DELETE|1|-1)$", message = "Invalid action")
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
