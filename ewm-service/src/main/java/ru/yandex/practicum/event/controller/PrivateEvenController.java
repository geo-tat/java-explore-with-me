package ru.yandex.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.event.dto.EventDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.UpdateEventUser;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@AllArgsConstructor
public class PrivateEvenController {
    private final EventService service;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    EventDto createEvent(@RequestBody @Valid NewEventDto newEventDto,
                         @PathVariable Long userId) {
        return service.createEvent(userId, newEventDto);
    }

    @GetMapping
    Collection<EventShortDto> getEventsByUser(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {

        return service.getEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    EventDto getEventById(@PathVariable Long userId,
                          @PathVariable Long eventId) {
        return service.getEventById(userId, eventId);
    }


    @PatchMapping("/{eventId}")
    EventDto updateEventByUser(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @RequestBody @Valid UpdateEventUser updateEvent) {
        return service.updateEvent(userId, eventId, updateEvent);
    }

    @GetMapping("/{eventId}/requests")
    Collection<ParticipationRequestDto> getRequestsForEvent(@PathVariable Long userId,
                                                            @PathVariable Long eventId) {
        return requestService.getRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    EventRequestStatusUpdateResult updateEventRequestStatus(@PathVariable Long userId,
                                                            @PathVariable Long eventId,
                                                            @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        return requestService.updateEventRequestStatus(userId, eventId, request);
    }
}
