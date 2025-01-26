package ru.feryafox.yetanotherkanbanboard.models.board.info;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link ru.feryafox.yetanotherkanbanboard.entities.Board}
 */
@Value
@Schema(description = "Информация о доске")
public class BoardInfoDto implements Serializable {
    @Schema(description = "Id доски", example = "123")
    @NotBlank(message = "Id доски не может быть пустой")
    Long id;

    @Schema(description = "Название доски", example = "Крутая доска")
    @NotBlank(message = "Название доски не может быть пустым")
    String title;

    @Schema(description = "Колонки доски")
    Set<ColumnDto> columns;

    @Schema(description = "Владелец доски")
    UserBoardInfoDto boardOwner;

    @Schema(description = "Имеют доступ")
    Set<UserBoardInfoDto> accessibleBoards ;
}