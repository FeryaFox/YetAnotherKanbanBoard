package ru.feryafox.yetanotherkanbanboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.feryafox.yetanotherkanbanboard.entities.Board;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query("""
    SELECT b FROM Board b
    LEFT JOIN FETCH b.columns c
    LEFT JOIN FETCH c.cards ca
    LEFT JOIN FETCH ca.userResponsible u
    WHERE b.id = :boardId AND (:userId IN (b.boardOwner.id, u.id))
    """)
    Optional<Board> findBoardWithAccess(@Param("boardId") Long boardId, @Param("userId") Long userId);

    Board getBoardById(Long id);
}
