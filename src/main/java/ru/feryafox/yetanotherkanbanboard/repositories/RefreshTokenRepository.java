package ru.feryafox.yetanotherkanbanboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.yetanotherkanbanboard.entities.RefreshToken;
import ru.feryafox.yetanotherkanbanboard.entities.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser_Username(String username);

    String user(User user);
}