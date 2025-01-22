package ru.feryafox.yetanotherkanbanboard.services;

import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.feryafox.yetanotherkanbanboard.entities.Board;
import ru.feryafox.yetanotherkanbanboard.entities.Column;
import ru.feryafox.yetanotherkanbanboard.models.column.UpdateColumnDto;
import ru.feryafox.yetanotherkanbanboard.repositories.ColumnRepository;
import ru.feryafox.yetanotherkanbanboard.entities.User;
import ru.feryafox.yetanotherkanbanboard.repositories.BoardRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.UserRepository;

@Service
public class ColumnService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;
    private final UserService userService;

    public ColumnService(UserRepository userRepository, BoardRepository boardRepository, ColumnRepository columnRepository, UserService userService) {
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
        this.userService = userService;
    }

    @Transactional
    public Column createColumn(String columnTitle, String createName, Long boardId) {
        if (!userService.isBoardOwner(boardId, createName)) throw new AccessDeniedException("You do not own the board");

        User user = userRepository.findByUsername(createName).orElseThrow(
                () -> new AccessDeniedException("User does not have access to this board")
        );
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new IllegalArgumentException("Board does not exist")
        );
        Column.ColumnBuilder builder = Column.builder();
        builder.title(columnTitle);
        builder.creator(user);
        builder.board(board);

        Column column = builder.build();

        return columnRepository.save(column);
    }

    @Transactional
    public boolean isColumnInBoard(Long boardId, Long columnId) {
        Column column = columnRepository.findById(columnId).orElseThrow(
                () -> new IllegalArgumentException("Board does not exist")
        );

        return column.getBoard().getId().equals(boardId);
    }

    public void updateColumn(String username, Long columnId, UpdateColumnDto updateColumnDto) {

        if (!userService.isColumnAccessible(columnId, username)) throw new AccessDeniedException("You do not have access to this column");

        Column column = columnRepository.findById(columnId).orElseThrow(
                () -> new IllegalArgumentException("Column does not exist")
        );

        column.setTitle(updateColumnDto.getTitle());

        columnRepository.save(column);

    }
}
