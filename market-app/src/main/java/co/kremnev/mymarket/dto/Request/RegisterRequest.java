package co.kremnev.mymarket.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Имя пользователя обязательно")
        String username,
        @NotBlank(message = "Пароль обязателен")
        @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
        String password
) {}
