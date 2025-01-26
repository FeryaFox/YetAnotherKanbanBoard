package ru.feryafox.yetanotherkanbanboard.services;

import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.feryafox.yetanotherkanbanboard.components.mapper.BoardMapper;
import ru.feryafox.yetanotherkanbanboard.entities.Board;
import ru.feryafox.yetanotherkanbanboard.entities.Column;
import ru.feryafox.yetanotherkanbanboard.entities.User;
import ru.feryafox.yetanotherkanbanboard.models.board.info.BoardInfoDto;
import ru.feryafox.yetanotherkanbanboard.models.board.CreateBoardDto;
import ru.feryafox.yetanotherkanbanboard.repositories.BoardRepository;
import ru.feryafox.yetanotherkanbanboard.repositories.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class BoardService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardMapper boardMapper;
    private final ColumnService columnService;
    private final UserService userService;

    public BoardService(UserRepository userRepository, BoardRepository boardRepository, BoardMapper boardMapper, ColumnService columnService, UserService userService) {
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.boardMapper = boardMapper;
        this.columnService = columnService;
        this.userService = userService;
    }

    public BoardInfoDto getBoardInfo(Long boardId, UserDetails userDetails) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());

        Board board = boardRepository.findBoardWithAccess(boardId, user.getId()).orElseThrow(
                () -> new AccessDeniedException("User does not have access to this board")
        );

        return boardMapper.toDto(board);

    }

    @Transactional
    public long createBoard(CreateBoardDto createBoardDto, UserDetails userDetails) {
        User owner = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<User> ownedUsers = new HashSet<>();
        ownedUsers.add(owner);


        Board.BoardBuilder boardBuilder = Board.builder();
        boardBuilder.title(createBoardDto.getTitle());
        boardBuilder.boardOwner(owner);
        boardBuilder.accessibleBoards(ownedUsers);

        Board board = boardBuilder.build();

        Board savedBoard = boardRepository.save(board);


        Set<Column> columns = new HashSet<>();

        columns.add(columnService.createColumn("Бэклог", owner.getUsername(), savedBoard.getId()));
        columns.add(columnService.createColumn("В процессе", owner.getUsername(), savedBoard.getId()));
        columns.add(columnService.createColumn("Выполнено", owner.getUsername(), savedBoard.getId()));

        savedBoard = boardRepository.save(board);

        return savedBoard.getId();
    }

    public void updateBoardTitle(Long boardId, String newTitle, UserDetails userDetails) {
        if (!userService.isBoardOwner(boardId, userDetails)) throw new AccessDeniedException("User does not have access to this board");

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new IllegalArgumentException("Board not found")
        );

        board.setTitle(newTitle);
        boardRepository.save(board);
    }

    public void deleteAccessibleToBoard(Long boardId, String username, String usernameToDelete) {
       userService.isBoardOwner(boardId, username);

       Board board = boardRepository.findById(boardId).orElseThrow(
               () -> new IllegalArgumentException("Board not found")
       );

       User user = userRepository.findUserByUsername(usernameToDelete);

       if (user == null) throw new AccessDeniedException("User not found");

       board.getAccessibleBoards().remove(user);

       boardRepository.save(board);
    }
}
