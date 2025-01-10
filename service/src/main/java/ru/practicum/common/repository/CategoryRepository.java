package ru.practicum.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.common.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}