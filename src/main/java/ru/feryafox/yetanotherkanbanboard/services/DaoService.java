package ru.feryafox.yetanotherkanbanboard.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.feryafox.yetanotherkanbanboard.entities.Board;
import ru.feryafox.yetanotherkanbanboard.entities.Card;
import ru.feryafox.yetanotherkanbanboard.entities.Column;
import ru.feryafox.yetanotherkanbanboard.entities.User;
import ru.feryafox.yetanotherkanbanboard.repositories.BoardRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.CardRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.ColumnRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class DaoService {

    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;

     public Column getColumn(Long columnId) {
        return columnRepository.findById(columnId).orElseThrow(
                () -> new IllegalArgumentException("Column does not exist")
        );
    }

    public Card getCard(Long id) {
        return cardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Card does not exist")
        );
    }

     public User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("User does not exist")
        );
    }

    public Board getBoard(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow(
                () -> new IllegalArgumentException("Board does not exist")
        );
    }
}
