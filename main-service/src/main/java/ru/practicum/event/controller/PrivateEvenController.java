package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUser;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@AllArgsConstructor
public class PrivateEvenController {
    private final EventService service;

    @PostMapping
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

    @GetMapping("{/eventId}")
    EventDto getEventById(@PathVariable Long userId,
                          @PathVariable Long eventId) {
        return service.getEventById(userId,eventId);
    }


    @PatchMapping("{/eventId}")
    EventDto updateEventByUser(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @RequestBody @Valid UpdateEventUser updateEvent) {
        return service.updateEvent(userId, eventId, updateEvent);
    }

    //   getParticipationRequestByIdEven

    //   public EventRequestStatusUpdateResult updateEventRequestStatus
}
