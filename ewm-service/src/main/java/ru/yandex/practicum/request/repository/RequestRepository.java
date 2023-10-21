package ru.yandex.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.request.model.Request;

import java.util.Collection;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Collection<Request> findAllByRequesterId(Long userId);

    Collection<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    @Query("SELECT COUNT(r.id) " +
            "FROM Request AS r " +
            "WHERE r.event.id = :eventId " +
            "AND r.state = 'CONFIRMED' ")
    Integer getConfirmedRequestsByEventId(Long eventId);

    @Query("SELECT COUNT(r.id) " +
            "FROM Request AS r " +
            "WHERE r.event.id IN :eventIds " +
            "AND r.state = 'CONFIRMED' ")
    List<Integer> getConfirmedRequestsByListOfEvents(List<Long> eventIds);
}
