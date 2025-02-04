package ru.practicum.general.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.general.model.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}