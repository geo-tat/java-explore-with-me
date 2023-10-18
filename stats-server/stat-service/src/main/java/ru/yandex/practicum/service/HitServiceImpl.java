package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.mapper.Mapper;
import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.dto.ViewStatsDto;
import ru.yandex.practicum.model.Hit;
import ru.yandex.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class HitServiceImpl implements HitService {
    private final HitRepository repository;

    @Override
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        log.info("**************************** Вызов метода saveHit");
        Hit hit = Mapper.toEntity(endpointHitDto);
        repository.save(hit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStatsDto> stats = new ArrayList<>();
        if (unique) {
            if (uris == null) {
                //   уникальные, но без списка uri
                stats = repository.getAllUniqueStats(start, end);
            } else {
                // уникальные и со списком uri
                stats = repository.getUniqueStatsByUrisAndTimestamps(start, end, uris);
            }
        } else {
            if (uris.isEmpty()) {
                //   не уникальные, но без списка uri
                stats = repository.getAllStats(start, end);

            } else {
                // неуникальные и со списком ури
                stats = repository.getStatsByUrisAndTimestamps(start, end, uris);
            }
        }
        return stats;

    }
}
