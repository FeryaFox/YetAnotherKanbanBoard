package ru.feryafox.yetanotherkanbanboard.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String surname;

    @NotBlank
    private String middleName;
}
