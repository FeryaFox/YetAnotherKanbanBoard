package ru.feryafox.yetanotherkanbanboard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.feryafox.yetanotherkanbanboard.entities.Column;

public interface ColumnRepository extends JpaRepository<Column, Long> {
    Column getColumnsById(Long id);
}