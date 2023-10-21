package ru.yandex.practicum.compilations.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.compilations.model.Compilation;

import java.util.Collection;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Collection<Compilation> findAllByPinned(Boolean pinned, PageRequest page);
}
