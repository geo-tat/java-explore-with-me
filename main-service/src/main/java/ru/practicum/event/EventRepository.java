package ru.practicum.event;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e " +
            "from Event AS e " +
            "JOIN FETCH e.initiator " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "where e.initiator.id = :userId"
    )
    List<Event> getEventsByUser(Long userId, PageRequest page);
}
