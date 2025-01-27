package ru.feryafox.yetanotherkanbanboard.exceptions.auth;

import jakarta.security.auth.message.AuthException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.feryafox.yetanotherkanbanboard.exceptions.ErrorMessage;

@Slf4j
@Getter
public class MissingUserAgentException extends AuthException {
    private final String ip;
    private static final String MESSAGE_TEMPLATE = "Запрос с ip %s не имеет заголовка";
    public static final String JSON_ERROR = "Missing User-Agent";
    public static final String JSON_ERROR_MESSAGE =  "User-Agent is required for request";
    public static final MissingUserAgentMessage MESSAGE = new MissingUserAgentMessage(JSON_ERROR, JSON_ERROR_MESSAGE);
    public static final String JSON_MESSAGE = "{\"error\":\"" + JSON_ERROR + "\",\"message\":\"" + JSON_ERROR_MESSAGE + "\"}";

    public MissingUserAgentException(String ip) {
        super(String.format(MESSAGE_TEMPLATE, ip));
        this.ip = ip;
    }

    @Getter
    public static class MissingUserAgentMessage extends ErrorMessage {
        public MissingUserAgentMessage(String error, String message) {
            super(error, message);
        }
    }
}
