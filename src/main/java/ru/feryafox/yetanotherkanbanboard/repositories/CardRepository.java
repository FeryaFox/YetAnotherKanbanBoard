package ru.feryafox.yetanotherkanbanboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.feryafox.yetanotherkanbanboard.entities.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

}