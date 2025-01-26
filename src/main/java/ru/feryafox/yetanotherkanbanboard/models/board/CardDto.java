package ru.feryafox.yetanotherkanbanboard.models.board;

import lombok.Value;
import ru.feryafox.yetanotherkanbanboard.entities.Card;
import ru.feryafox.yetanotherkanbanboard.entities.User;

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