package ru.feryafox.yetanotherkanbanboard.controllers.auth;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.Column;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.yetanotherkanbanboard.components.utils.IpGetter;
import ru.feryafox.yetanotherkanbanboard.exceptions.auth.MissingUserAgentException;
import ru.feryafox.yetanotherkanbanboard.models.auth.AuthResponse;
import ru.feryafox.yetanotherkanbanboard.models.auth.LoginRequest;
import ru.feryafox.yetanotherkanbanboard.models.auth.RefreshRequest;
import ru.feryafox.yetanotherkanbanboard.models.auth.RegistrationRequest;
import ru.feryafox.yetanotherkanbanboard.services.UserService;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UserService userService;
    private final IpGetter ipGetter;

    public AuthController(UserService userService, IpGetter ipGetter) {
        this.userService = userService;
        this.ipGetter = ipGetter;
    }

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Логин в систему. Получение токена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(
                                    name = "Пример успешной авторизации",
                                    summary = "Пример успешной авторизации",
                                    value = AuthResponse.JSON_EXAMPLE
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) throws MissingUserAgentException {
        return ResponseEntity.ok(userService.login(loginRequest, userAgent, request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "Authorization") String authorization,
            HttpServletRequest request
            ) throws MissingUserAgentException {
        AuthResponse authResponse = userService.refresh(authorization, userAgent, request);

        if (authResponse != null) return ResponseEntity.ok(authResponse);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestBody RegistrationRequest registrationRequest,
            HttpServletRequest request
    ) throws MissingUserAgentException {
        AuthResponse authResponse = userService.register(registrationRequest, userAgent, request);
        if (authResponse == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken");
        }

        return ResponseEntity.status(HttpStatus.OK).body(authResponse);
    }
}
