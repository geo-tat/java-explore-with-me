package ru.practicum.mapper;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.Hit;

public class Mapper {
    public static Hit toEntity(EndpointHitDto dto) {
        return Hit.builder()
                .ip(dto.getIp())
                .uri(dto.getUri())
                .app(dto.getApp())
                .date(dto.getTimestamp())
                .build();
    }

    public static ViewStatsDto toViewDto(Hit hit) {
        return ViewStatsDto.builder()
                .uri(hit.getUri())
                .app(hit.getApp())
                .build();
    }
}
