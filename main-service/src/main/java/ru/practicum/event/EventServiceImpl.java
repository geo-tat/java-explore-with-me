package ru.practicum.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.CategoryRepository;
import ru.practicum.category.CategoryService;
import ru.practicum.event.dto.*;
import ru.practicum.exception.EntityNotFoundException;
import ru.practicum.exception.ValidateDateException;
import ru.practicum.exception.ValidationUpdateException;
import ru.practicum.user.User;
import ru.practicum.user.UserMapper;
import ru.practicum.user.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;


    @Override
    public EventDto createEvent(Long userId, NewEventDto newEventDto) {
        validDate(newEventDto.getEventDate());
        User user = UserMapper.toEntity(userService.getById(userId));
        Category category = CategoryMapper.toEntity(categoryService.getCategoryById(newEventDto.getCategory()));
        Event eventToSave = EventMapper.toEntity(newEventDto);
        eventToSave.setInitiator(user);
        eventToSave.setCategory(category);
        return EventMapper.toDto(repository.save(eventToSave));
    }

    @Override
    public Collection<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        User user = UserMapper.toEntity(userService.getById(userId));
        PageRequest page = PageRequest.of(from / size, size);

        return repository.getEventsByUser(userId,page).stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());

        // как должны сортироваться?
    }

    @Override
    public EventDto getEventById(Long userId, Long eventId) {
        User user = UserMapper.toEntity(userService.getById(userId));
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + eventId + " not found!"));
        return EventMapper.toDto(event);
    }

    @Override
    public EventDto updateEvent(Long userId, Long eventId, UpdateEventUser updateEvent) {
        Event eventToUpdate = repository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + eventId + " not found!"));
        validDate(updateEvent.getEventDate());
        if(eventToUpdate.getState() == StateEvent.PUBLISHED) {
            throw new ValidationUpdateException("State of event do not have permission to update");
        } else {
         return update(eventToUpdate,updateEvent);
        }

    }

    private EventDto update(Event eventToUpdate, UpdateEventUser updateEvent) {
        if (updateEvent.getAnnotation() != null) {
            eventToUpdate.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            Category category = CategoryMapper.toEntity(categoryService.getCategoryById(updateEvent.getCategory()));
            eventToUpdate.setCategory(category);
        }
        if (updateEvent.getDescription() != null) {
            eventToUpdate.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            eventToUpdate.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getPaid() != null) {
            eventToUpdate.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            eventToUpdate.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            eventToUpdate.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getTitle() != null) {
            eventToUpdate.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction() == StateAction.CANCEL_REVIEW) {
                eventToUpdate.setState(StateEvent.CANCELLED);
            }
            if (updateEvent.getStateAction() == StateAction.SEND_TO_REVIEW) {
                eventToUpdate.setState(StateEvent.PENDING);
            }
        }
        if (updateEvent.getLocation() != null) {
            eventToUpdate.setLocation(updateEvent.getLocation());
        }
        return EventMapper.toDto(eventToUpdate);
    }

    @Override
    public Collection<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable, String sort, Integer from,
                                                     Integer size, HttpServletRequest request) {
      //  PageRequest page = PageRequest.of(from / size, size);
        if(rangeEnd != null && rangeStart != null) {
            if(rangeEnd.isBefore(rangeStart))
                throw new ValidateDateException("We can't go back in future. Check time range");
        }
        List<Event> events;
        if(sort.equals("EVENT_DATE")) {
            PageRequest page = PageRequest.of(from / size, size, Sort.by("eventDate"));

        }


        return null;
    }

    @Override
    public EventDto getEventByIdPublic(Long id, HttpServletRequest request) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + id + " not found!"));

        return null;
    }

    @Override
    public Collection<EventDto> getEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventDto updateEventAdmin(Long eventId, UpdateEventAdmin updateEventAdmin) {
        return null;
    }

    private void validDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateDateException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }
    }
}
