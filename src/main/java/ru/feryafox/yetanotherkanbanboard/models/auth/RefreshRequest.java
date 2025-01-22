package ru.feryafox.yetanotherkanbanboard.models.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Отправка рефреш токена")
public class RefreshRequest {
    @Schema(description = "Refresh Token")
    @NotBlank(message = "Refresh token не может быть пустым.")
    private String refreshToken;
}
