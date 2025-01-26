package ru.feryafox.yetanotherkanbanboard.models.board.info;

import lombok.Value;
import ru.feryafox.yetanotherkanbanboard.entities.Card;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link Card}
 */
@Value
public class CardDto implements Serializable {
    Long id;
    String title;
    String content;
    Set<UserBoardInfoDto> userResponsible;
    Integer position;
    LocalDateTime createdAt;

// TODO добавить Swagger Аннотации
}