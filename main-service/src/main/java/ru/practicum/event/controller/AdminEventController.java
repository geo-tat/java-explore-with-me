package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventState;
import ru.practicum.event.dto.UpdateEventAdmin;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.executable.ValidateOnExecution;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@AllArgsConstructor
public class AdminEventController {
    private final EventService service;

    @GetMapping
    public Collection<EventDto> getEvents( @RequestParam(required = false) List<Long> users,

                                           // список состояний в которых находятся искомые события
                                           @RequestParam(required = false) List<EventState> states,

                                           // список id категорий в которых будет вестись поиск
                                           @RequestParam(required = false) List<Long> categories,

                                           // дата и время не раньше которых должно произойти событие
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,

                                           // дата и время не позже которых должно произойти событие
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = "10") @Positive Integer size) {
        return service.getEventsAdmin(users,states,categories,rangeStart,rangeEnd,from,size);
    }

    @PatchMapping("{/eventId}")
    public EventDto updateAdmin(@PathVariable Long eventId,
                                @RequestBody @Valid UpdateEventAdmin updateEventAdmin) {
        return service.updateEventAdmin(eventId, updateEventAdmin);
    }
}
