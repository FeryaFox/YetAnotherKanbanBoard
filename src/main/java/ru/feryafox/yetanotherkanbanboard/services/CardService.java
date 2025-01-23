package ru.feryafox.yetanotherkanbanboard.services;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.feryafox.yetanotherkanbanboard.entities.*;
import ru.feryafox.yetanotherkanbanboard.models.card.CreateCardDto;
import ru.feryafox.yetanotherkanbanboard.models.card.UpdateCardDto;
import ru.feryafox.yetanotherkanbanboard.repositories.BoardRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.CardRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.ColumnRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.UserRepository;

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
}
