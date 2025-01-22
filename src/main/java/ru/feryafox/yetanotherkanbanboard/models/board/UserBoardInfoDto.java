package ru.feryafox.yetanotherkanbanboard.models.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import ru.feryafox.yetanotherkanbanboard.entities.User;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
@Value
public class UserBoardInfoDto implements Serializable {
    // TODO добавить Swagger Аннотации
    Long id;
    String username;
    String firstName;
    String surname;
    String middleName;
}