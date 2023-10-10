package ru.practicum.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   // Collection<User> findAllById(List<Long> idsToRep, PageRequest page);

    Collection<User> findAllByIdIn(List<Long> idsToRep, PageRequest page);
    //  Collection<User> findAll(PageRequest pageRequest);
}

