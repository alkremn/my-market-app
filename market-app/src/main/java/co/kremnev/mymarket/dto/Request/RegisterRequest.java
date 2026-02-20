package co.kremnev.mymarket.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "Имя пользователя обязательно")
        String username,
        @NotBlank(message = "Пароль обязателен")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#@$?*!%&-])[A-Za-z\\d#@$?*!%&-]{8,40}$",
                message = "Пароль 8-40 символов, заглавная и строчная буква, цифра и спецсимвол (#@$?*!%&-)")
        String password
) {}
