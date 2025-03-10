package ru.feryafox.yetanotherkanbanboard.models.board.info;

import lombok.Value;
import ru.feryafox.yetanotherkanbanboard.entities.Column;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link Column}
 */
@Value
public class ColumnDto implements Serializable {
    Long id;
    String title;
    Set<CardDto> cards;
    // TODO добавить Swagger Аннотации
}