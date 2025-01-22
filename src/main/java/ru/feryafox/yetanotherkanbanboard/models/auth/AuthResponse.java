package ru.feryafox.yetanotherkanbanboard.models.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Токены после авторизации")
public class AuthResponse {
    @Schema(description = "Токен")
    private String token;

    @Schema(description = "Рефреш токен")
    private String refreshToken;
}
