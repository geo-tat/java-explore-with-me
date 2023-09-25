package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(h.app,h.uri, COUNT (h.ip)) "
            + "FROM Hit AS h "
            + "WHERE h.date >= :start AND h.date <= :end AND h.uri IN :uris "
            + "GROUP BY h.app, h.uri "
            + "ORDER BY COUNT (h.ip) DESC ")
    List<ViewStatsDto> getStatsByUrisAndTimestamps(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end,
                                                   @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(h.app,h.uri,  COUNT (distinct h.ip)) "
            + "FROM Hit AS h "
            + "WHERE h.date >= :start AND h.date <= :end AND h.uri IN :uris "
            + "GROUP BY h.app, h.uri "
            + "ORDER BY COUNT (DISTINCT h.ip) DESC ")
    List<ViewStatsDto> getUniqueStatsByUrisAndTimestamps(@Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end,
                                                         @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(h.app,h.uri,  COUNT (h.ip)) "
            + "FROM Hit AS h "
            + "WHERE h.date >= :start AND h.date <= :end "
            + "GROUP BY h.app, h.uri "
            + "ORDER BY COUNT (h.ip) DESC ")
    List<ViewStatsDto> getAllStats(@Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStatsDto(h.app,h.uri,  COUNT (distinct h.ip)) "
            + "FROM Hit AS h "
            + "WHERE h.date >= :start AND h.date <= :end "
            + "GROUP BY h.app, h.uri "
            + "ORDER BY COUNT (DISTINCT h.ip) DESC ")
    List<ViewStatsDto> getAllUniqueStats(@Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);
}
