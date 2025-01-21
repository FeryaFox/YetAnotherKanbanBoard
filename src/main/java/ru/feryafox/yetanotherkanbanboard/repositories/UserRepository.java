package ru.feryafox.yetanotherkanbanboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.yetanotherkanbanboard.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
