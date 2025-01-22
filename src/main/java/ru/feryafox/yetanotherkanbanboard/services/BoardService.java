package ru.feryafox.yetanotherkanbanboard.services;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.feryafox.yetanotherkanbanboard.components.mapper.BoardMapper;
import ru.feryafox.yetanotherkanbanboard.entities.Board;
import ru.feryafox.yetanotherkanbanboard.entities.User;
import ru.feryafox.yetanotherkanbanboard.models.board.BoardInfoDto;
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

    public BoardService(UserRepository userRepository, BoardRepository boardRepository, BoardMapper boardMapper) {
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.boardMapper = boardMapper;
    }

    public BoardInfoDto getBoardInfo(Long boardId, UserDetails userDetails) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());

        Board board = boardRepository.findBoardWithAccess(boardId, user.getId()).orElseThrow(
                () -> new AccessDeniedException("User does not have access to this board")
        );

        return boardMapper.toDto(board);

    }
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
        return savedBoard.getId();
    }

    public void updateBoardTitle(Long boardId, String newTitle, UserDetails userDetails) {
        if (!isBoardOwner(boardId, userDetails)) return;

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new IllegalArgumentException("Board not found")
        );

        board.setTitle(newTitle);
        boardRepository.save(board);
    }

    private boolean isBoardOwner(Long boardId, UserDetails userDetails) {
        User user = userRepository.findUserByUsername(userDetails.getUsername());

        Board board = boardRepository.findBoardWithAccess(boardId, user.getId()).orElse(null);

        return board != null;
    }
}
