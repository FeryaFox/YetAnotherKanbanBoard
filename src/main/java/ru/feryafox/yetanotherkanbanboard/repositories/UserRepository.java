package ru.feryafox.yetanotherkanbanboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.feryafox.yetanotherkanbanboard.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    User findUserByUsername(String username);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.cardsOwned c
        WHERE u.username = :username AND c.id = :cardId
    """)
    Optional<User> getUserByCardId(@Param("username") String username, @Param("cardId") Long cardId);


    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.createdColumns c
        WHERE u.username = :username AND c.id = :columnId
    """)
    Optional<User> getUserByColumnId(@Param("username") String username, @Param("colunmId") Long columnId);
}
