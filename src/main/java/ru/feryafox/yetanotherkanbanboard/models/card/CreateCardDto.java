package ru.feryafox.yetanotherkanbanboard.models.card;

import lombok.Data;

@Data
public class CreateCardDto {
    private String title;
    private String content;
    private Long boardId;
    private Long columnId;
}
