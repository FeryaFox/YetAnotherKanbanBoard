package ru.feryafox.yetanotherkanbanboard.models.card;

import lombok.Data;

@Data
public class MoveCardDto {
    private Long newColumn;
    private Integer newPosition;
}
