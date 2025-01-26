package ru.feryafox.yetanotherkanbanboard.models.board.info;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import ru.feryafox.yetanotherkanbanboard.entities.User;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
@Schema(description = "Возвращает информацию о пользователя.")
public class UserBoardInfoDto implements Serializable {
    @Schema(description = "Id пользователя", example = "123123")
    @NotBlank(message = "Id пользователя не может быть пустым")
    Long id;

    @Schema(description = "Имя пользователя", example = "super_username")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    String login;


    @Schema(description = "Имя", example = "Иванов")
    @NotBlank(message = "Имя не может быть пустым")
    String name;

    @Schema(description = "Фамилия", example = "Иванов")
    @NotBlank(message = "Фамилия не может быть пустой")
    String surname;

    @Schema(description = "Отчество", example = "Иванович")
    @NotBlank(message = "Отчество не может быть пустым")
    String middleName;

}