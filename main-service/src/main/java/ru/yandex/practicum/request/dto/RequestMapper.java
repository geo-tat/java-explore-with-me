package ru.yandex.practicum.request.dto;

import ru.yandex.practicum.request.model.Request;

public class RequestMapper {

    public static ParticipationRequestDto toDto(Request request) {
        return ParticipationRequestDto
                .builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getState())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .build();
    }
}
