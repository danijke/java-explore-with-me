package ru.practicum.ewm.main.compilation.repository;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.compilation.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    boolean existsByTitleIgnoreCase(String title);

    Page<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);
}
