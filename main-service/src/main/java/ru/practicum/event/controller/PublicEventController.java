package ru.practicum.event.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.EventService;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class PublicEventController {
    private final EventService service;

    @GetMapping
    public Collection<EventShortDto> getEvents( // текст для поиска в содержимом аннотации и подробном описании события
                                                @RequestParam(required = false) String text,

                                                // список идентификаторов категорий в которых будет вестись поиск
                                                @RequestParam(required = false) List<Long> categories,

                                                // поиск только платных/бесплатных событий
                                                @RequestParam(required = false) Boolean paid,

                                                // дата и время не раньше которых должно произойти событие
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,

                                                // дата и время не позже которых должно произойти событие
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,

                                                // только события у которых не исчерпан лимит запросов на участие
                                                @RequestParam(defaultValue = "false") Boolean onlyAvailable,

                                                // Вариант сортировки: по дате события или по количеству просмотров
                                                @RequestParam(defaultValue = "EVENT_DATE") String sort,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = "10") @Positive Integer size,
                                                HttpServletRequest request) {
        return service.getEventsPublic(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request);

    }
        @GetMapping("{/id}")
    public EventDto getEventByIdPublic(@PathVariable Long id,
                                       HttpServletRequest request) {
        return service.getEventByIdPublic(id,request);
        }

}
