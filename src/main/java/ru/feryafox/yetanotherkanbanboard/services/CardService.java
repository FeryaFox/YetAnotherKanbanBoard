package ru.feryafox.yetanotherkanbanboard.services;

import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.feryafox.yetanotherkanbanboard.entities.*;
import ru.feryafox.yetanotherkanbanboard.models.card.CreateCardDto;
import ru.feryafox.yetanotherkanbanboard.models.card.MoveCardDto;
import ru.feryafox.yetanotherkanbanboard.models.card.UpdateCardDto;
import ru.feryafox.yetanotherkanbanboard.repositories.BoardRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.CardRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.ColumnRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.UserRepository;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {
    private final BoardService boardService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;
    private final ColumnService columnService;
    private final CardRepository cardRepository;
    private final UserService userService;

    public CardService(BoardService boardService, UserRepository userRepository, BoardRepository boardRepository, ColumnRepository columnRepository, ColumnService columnService,
                       CardRepository cardRepository, UserService userService) {
        this.boardService = boardService;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.columnService = columnService;
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    public Long createCard(CreateCardDto createCardDto, String username){
        if (!userService.isBoardOwner(createCardDto.getBoardId(), username)) throw new AccessDeniedException("User does not have access to this board");
        if (!columnService.isColumnInBoard(createCardDto.getBoardId(), createCardDto.getColumnId())) throw new AccessDeniedException("Column does not exist");

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("User does not exist")
        );

        Column column = columnRepository.getColumnsById(createCardDto.getColumnId());

        Card.CardBuilder cardBuilder = Card.builder();

        cardBuilder.title(createCardDto.getTitle());
        cardBuilder.content(createCardDto.getContent());
        cardBuilder.column(column);
        cardBuilder.userOwner(user);
        cardBuilder.position(column.getCards().size() + 1);

        Card savedCard = cardRepository.save(cardBuilder.build());

        return savedCard.getId();
    }


    public void updateCard(Long cardId, UpdateCardDto updateCardDto, String username){
        if (!userService.isCardAccessible(cardId, username)) throw new AccessDeniedException("User does not have access to this board");

        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new IllegalArgumentException("Card does not exist")
        );

        card.setTitle(updateCardDto.getTitle());
        card.setContent(updateCardDto.getDescription());

        cardRepository.save(card);
    }

    @Transactional
    public void moveCard(Long cardId, MoveCardDto moveCardDto, String username){

        validateAccess(cardId, moveCardDto.getNewColumn(), username);

        Card card = getCard(cardId);

        Column oldColumn = card.getColumn();
        Column newColumn = getColumn(moveCardDto.getNewColumn());

        normalizePositions(oldColumn);
        if (!oldColumn.equals(newColumn)) {
            normalizePositions(newColumn);
        }

        updateCardPositions(oldColumn, newColumn, card.getPosition(), moveCardDto.getNewPosition());

        card.setColumn(newColumn);
        card.setPosition(moveCardDto.getNewPosition());

        columnRepository.saveAll(Arrays.asList(oldColumn, newColumn));
        cardRepository.save(card);
    }

    private Column getColumn(Long columnId) {
        return columnRepository.findById(columnId).orElseThrow(
                () -> new IllegalArgumentException("Column does not exist")
        );
    }

    private Card getCard(Long id) {
        return cardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Card does not exist")
        );
    }

    private void updateCardPositions(Column oldColumn, Column newColumn, Integer oldPosition, Integer newPosition) {
        if (oldColumn.equals(newColumn)) {
            for (Card c : oldColumn.getCards()) {
                if (c.getPosition() >= newPosition && c.getPosition() < oldPosition) {
                    c.setPosition(c.getPosition() + 1);
                } else if (c.getPosition() > oldPosition && c.getPosition() <= newPosition) {
                    c.setPosition(c.getPosition() - 1);
                }
            }
        } else {
            for (Card c : oldColumn.getCards()) {
                if (c.getPosition() > oldPosition) {
                    c.setPosition(c.getPosition() - 1);
                }
            }
            for (Card c : newColumn.getCards()) {
                if (c.getPosition() >= newPosition) {
                    c.setPosition(c.getPosition() + 1);
                }
            }
        }
    }

    private void normalizePositions(Column column) {
        List<Card> sortedCards = column.getCards().stream()
                .sorted(Comparator.comparing(Card::getPosition))
                .toList();

        for (int i = 0; i < sortedCards.size(); i++) {
            Card card = sortedCards.get(i);
            card.setPosition(i + 1);
        }
    }

    private void validateAccess(Long cardId, Long columnId, String username) {
        if (!userService.isCardAccessible(cardId, username)) {
            throw new AccessDeniedException("User does not have access to this card");
        }
        if (!userService.isColumnAccessible(columnId, username)) {
            throw new AccessDeniedException("User does not have access to this column");
        }
    }
}
