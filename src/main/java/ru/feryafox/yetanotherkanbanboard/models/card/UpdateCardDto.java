package ru.feryafox.yetanotherkanbanboard.models.card;

import lombok.Data;

@Data
public class UpdateCardDto {
    private String title;
    private String description;
    // TODO добавить ответственного
}
