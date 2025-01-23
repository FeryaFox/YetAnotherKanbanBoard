package ru.feryafox.yetanotherkanbanboard.components.mapper;

import org.springframework.stereotype.Component;
import ru.feryafox.yetanotherkanbanboard.entities.Board;
import ru.feryafox.yetanotherkanbanboard.entities.Card;
import ru.feryafox.yetanotherkanbanboard.entities.Column;
import ru.feryafox.yetanotherkanbanboard.entities.User;
import ru.feryafox.yetanotherkanbanboard.models.board.BoardInfoDto;
import ru.feryafox.yetanotherkanbanboard.models.board.CardDto;
import ru.feryafox.yetanotherkanbanboard.models.board.ColumnDto;
import ru.feryafox.yetanotherkanbanboard.models.board.UserBoardInfoDto;

import java.util.stream.Collectors;

@Component
public class BoardMapper {

    public BoardInfoDto toDto(Board board) {
        return new BoardInfoDto(
                board.getId(),
                board.getTitle(),
                board.getColumns().stream()
                        .map(this::toDto)
                        .collect(Collectors.toSet()),
                toDto(board.getBoardOwner())
        );
    }

    private ColumnDto toDto(Column column) {
        return new ColumnDto(
                column.getId(),
                column.getTitle(),
                column.getCards().stream()
                        .map(this::toDto)
                        .collect(Collectors.toSet())
        );
    }

    private CardDto toDto(Card card) {
        return new CardDto(
                card.getId(),
                card.getTitle(),
                card.getContent(),
                card.getUserResponsible().stream()
                        .map(this::toDto)
                        .collect(Collectors.toSet()),
                card.getPosition()
        );
    }

    private UserBoardInfoDto toDto(User user) {
        return new UserBoardInfoDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getSurname(),
                user.getMiddleName()
        );
    }
}

