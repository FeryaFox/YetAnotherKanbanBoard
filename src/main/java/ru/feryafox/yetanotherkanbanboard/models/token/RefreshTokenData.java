package ru.feryafox.yetanotherkanbanboard.models.token;

import lombok.Data;

import java.util.Set;

@Data
public class RefreshTokenData {
    private Set<String> userAgents;
    private String username;
    private String refreshToken;
}
