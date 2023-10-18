package ru.yandex.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.request.model.Request;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Collection<Request> findAllByRequesterId(Long userId);

    Collection<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);
}
