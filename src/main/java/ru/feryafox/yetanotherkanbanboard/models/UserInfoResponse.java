package ru.feryafox.yetanotherkanbanboard.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Возвращает информацию о пользователя.")
public class UserInfoResponse {
    @Schema(description = "Id пользователя", example = "123123")
    @NotBlank(message = "Id пользователя не может быть пустым")
    private Long id;

    @Schema(description = "Имя пользователя", example = "super_username")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String login;


    @Schema(description = "Имя", example = "Иванов")
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @Schema(description = "Фамилия", example = "Иванов")
    @NotBlank(message = "Фамилия не может быть пустой")
    private String surname;

    @Schema(description = "Отчество", example = "Иванович")
    @NotBlank(message = "Отчество не может быть пустым")
    private String middleName;
}
