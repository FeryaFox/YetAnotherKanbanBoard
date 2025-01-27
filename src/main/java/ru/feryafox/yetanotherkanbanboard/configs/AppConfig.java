package ru.feryafox.yetanotherkanbanboard.configs;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
public class AppConfig {
    private String secret;
    private Long jwtExpirationMs;
    private Long refreshTokenExpirationMs;
    private Integer maxSessions;
}
