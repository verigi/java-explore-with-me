package ru.practicum.general.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.general.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("SELECT c FROM Compilation c " +
            "WHERE c.pinned = true")
    List<Compilation> findAllPinned(Pageable pageable);

    @Query("SELECT c FROM Compilation c " +
            "WHERE c.pinned = false")
    List<Compilation> findAllUnpinned(Pageable pageable);


}