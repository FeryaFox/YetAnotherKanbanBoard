package ru.feryafox.yetanotherkanbanboard.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
public class ErrorMessage {
    private final String error;
    private final String message;

    public ErrorMessage(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public static String getJsonMessage(ErrorMessage errorMessage) {
        return "{\"error\":\"" + errorMessage.getError() + "\",\"message\":\"" + errorMessage.getMessage() + "\"}";
    }
}
