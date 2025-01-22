package ru.feryafox.yetanotherkanbanboard.models.column;

import lombok.Data;

@Data
public class CreateColumnDto {
    private String columnTitle;
    private Long boardId;
}
