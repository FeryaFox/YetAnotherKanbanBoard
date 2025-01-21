package ru.feryafox.yetanotherkanbanboard.models;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}
