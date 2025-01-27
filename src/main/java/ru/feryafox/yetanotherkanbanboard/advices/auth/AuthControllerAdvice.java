package ru.feryafox.yetanotherkanbanboard.advices.auth;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.feryafox.yetanotherkanbanboard.exceptions.ErrorMessage;
import ru.feryafox.yetanotherkanbanboard.exceptions.auth.MissingUserAgentException;

@ControllerAdvice
public class AuthControllerAdvice {


    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Missing User-Agent header",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MissingUserAgentException.MissingUserAgentMessage.class),
                            examples = @ExampleObject(
                                    name = "Missing User-Agent Example",
                                    summary = "Пример ошибки отсутствия User-Agent",
                                    value = MissingUserAgentException.JSON_MESSAGE
                            )
                    )
            )
    })
    @ExceptionHandler(MissingUserAgentException.class)
    public ResponseEntity<?> handleMissingUserAgentException(MissingUserAgentException e) {
        System.out.println(11);
        return new ResponseEntity<>(MissingUserAgentException.MESSAGE, HttpStatus.BAD_REQUEST);
    }
}
