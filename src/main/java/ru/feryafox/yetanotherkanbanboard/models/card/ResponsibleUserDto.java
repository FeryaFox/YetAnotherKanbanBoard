package ru.feryafox.yetanotherkanbanboard.models.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.feryafox.yetanotherkanbanboard.entities.Card;
import ru.feryafox.yetanotherkanbanboard.entities.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsibleUserDto {
    private String username;
    private String name;
    private String surname;
    private String middleName;
    private Long cardId;

    public static ResponsibleUserDto from(User user, Card card) {
        ResponsibleUserDto.ResponsibleUserDtoBuilder builder = ResponsibleUserDto.builder();
        builder.username(user.getUsername());
        builder.name(user.getFirstName());
        builder.surname(user.getSurname());
        builder.middleName(user.getMiddleName());
        builder.cardId(card.getId());
        return builder.build();
    }
}
