package ru.yandex.practicum.location;

public class LocationMapper {
    public static Location toEntity(LocationDto dto) {
        return Location.builder()
                .lon(dto.getLon())
                .lat(dto.getLat())
                .build();
    }

    public static LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
