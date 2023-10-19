package ru.yandex.practicum.event.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.StatsClient;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.category.dto.CategoryMapper;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.category.service.CategoryService;
import ru.yandex.practicum.dto.EndpointHitDto;
import ru.yandex.practicum.dto.ViewStatsDto;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.event.dto.*;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.StateAction;
import ru.yandex.practicum.event.model.StateEvent;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.ValidateDateException;
import ru.yandex.practicum.exception.ValidateDateRangeException;
import ru.yandex.practicum.exception.ValidationUpdateException;
import ru.yandex.practicum.location.Location;
import ru.yandex.practicum.location.LocationMapper;
import ru.yandex.practicum.location.LocationRepository;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.event.model.StateEvent.PUBLISHED;

@Service
@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository repository;
    private final CategoryService categoryService;
    private final StatsClient statsClient;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public EventDto createEvent(Long userId, NewEventDto newEventDto) {
        validDate(newEventDto.getEventDate());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Категория с Id=" + newEventDto.getCategory() + " не найдена!"));
        Location location = locationRepository.save(LocationMapper.toEntity(newEventDto.getLocation()));
        Event eventToSave = EventMapper.toEntity(newEventDto);
        eventToSave.setInitiator(user);
        eventToSave.setCategory(category);
        eventToSave.setLocation(location);
        eventToSave.setState(StateEvent.PENDING);
        eventToSave.setConfirmedRequests(0);
        if (eventToSave.getPaid() == null) {
            eventToSave.setPaid(false);
        }
        if (eventToSave.getParticipantLimit() == null) {
            eventToSave.setParticipantLimit(0);
        }
        if (eventToSave.getRequestModeration() == null) {
            eventToSave.setRequestModeration(true);
        }
        return EventMapper.toDto(repository.save(eventToSave));
    }

    @Override
    public Collection<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));
        PageRequest page = PageRequest.of(from / size, size);

        return repository.getEventsByUser(userId, page).stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());

        // как должны сортироваться?
    }

    @Override
    public EventDto getEventById(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));
        Event event = repository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + eventId + " not found!"));
        return EventMapper.toDto(event);
    }

    @Override
    public EventDto updateEvent(Long userId, Long eventId, UpdateEventUser updateEvent) {
        Event eventToUpdate = repository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + eventId + " not found!"));
        //    validDate(updateEvent.getEventDate());
        if (eventToUpdate.getState() == PUBLISHED) {
            throw new ValidationUpdateException("State of event do not have permission to update");
        } else {
            return update(eventToUpdate, updateEvent);
        }

    }


    @Override
    public Collection<EventShortDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable, String sort, Integer from,
                                                     Integer size, HttpServletRequest request) {
        PageRequest page = PageRequest.of(from / size, size, Sort.unsorted());
        if (text == null && categories == null && paid == null && rangeStart == null && rangeEnd == null) {
            saveStats(request);
            return new ArrayList<>();
        }
        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart))
                throw new ValidateDateRangeException("Конфликт временной выборки");
        }
        List<Event> events;
        if (sort.equals("EVENT_DATE")) {
            page = PageRequest.of(from / size, size, Sort.by("eventDate"));

        }
        if (onlyAvailable) {
            events = repository.findAllPublishStateNotOnlyAvailable(rangeStart, rangeEnd, categories,
                    paid, text, page);
        } else {
            events = repository.findAllPublishStateOnlyAvailable(rangeStart,
                    rangeEnd,
                    categories,
                    paid,
                    text,
                    page);
        }
        events.forEach(event -> event.setViews(setViewsInEvent(event)));   // добавил просмотры
        saveStats(request);     // сохранил запрос в сервер статистики

        if (sort.equals("VIEWS")) {
            return events.stream()
                    .map(EventMapper::toShortDto)
                    .sorted(Comparator.comparingLong(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());
        }
        return events
                .stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto getEventByIdPublic(Long id, HttpServletRequest request) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + id + " not found!"));
        if (!event.getState().equals(PUBLISHED)) {
            throw new EntityNotFoundException("Event id=" + id + " not found!");
        }
        event.setViews(setViewsInEvent(event));
        saveStats(request);
        return EventMapper.toDto(event);
    }


    @Override
    public Collection<EventDto> getEventsAdmin(List<Long> users,
                                               List<StateEvent> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {

        if (rangeEnd != null && rangeStart != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new ValidateDateException("We can't go back in future. Check time range");
            }
        }
        PageRequest page = PageRequest.of(from / size, size);
        List<Event> events = repository.findAllAdmin(users, states, categories, rangeStart, rangeEnd, page);
        return events.stream()
                .map(EventMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public EventDto updateEventAdmin(Long eventId, UpdateEventAdmin updateEventAdmin) {
        Event eventToUpdate = repository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + eventId + " not found!"));
        if (eventToUpdate.getState() == PUBLISHED) {
            throw new ValidationUpdateException("State of event do not have permission to update");
        }
        return updateAdmin(eventToUpdate, updateEventAdmin);
    }

    private void validDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidateDateRangeException("Дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента");
        }
    }

    private long setViewsInEvent(Event event) {
        long views;
        List<String> uri = List.of("/events/" + event.getId());
        List<ViewStatsDto> viewStats = statsClient.getStats(event.getCreated(),
                LocalDateTime.now(), uri, true);
        if (viewStats.isEmpty()) {
            return 0;
        } else {
            views = viewStats.get(0).getHits();
        }
        return views;
    }


    private void saveStats(HttpServletRequest request) {
        statsClient.saveStats(EndpointHitDto.builder()
                .uri(request.getRequestURI())
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());


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
            validDate(updateEvent.getEventDate());
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
                eventToUpdate.setState(StateEvent.CANCELED);
            }
            if (updateEvent.getStateAction() == StateAction.SEND_TO_REVIEW) {
                eventToUpdate.setState(StateEvent.PENDING);
            }
        }
        if (updateEvent.getLocation() != null) {
            Location location = locationRepository.save(LocationMapper.toEntity(updateEvent.getLocation()));
            eventToUpdate.setLocation(location);
        }
        repository.save(eventToUpdate);
        return EventMapper.toDto(eventToUpdate);
    }

    private EventDto updateAdmin(Event eventToUpdate, UpdateEventAdmin updatedEvent) {
        if (updatedEvent.getAnnotation() != null) {
            eventToUpdate.setAnnotation(updatedEvent.getAnnotation());
        }
        if (updatedEvent.getDescription() != null) {
            eventToUpdate.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getCategory() != null) {
            Category category = CategoryMapper.toEntity(categoryService.getCategoryById(updatedEvent.getCategory()));
            eventToUpdate.setCategory(category);
        }
        if (updatedEvent.getPaid() != null) {
            eventToUpdate.setPaid(updatedEvent.getPaid());
        }
        if (updatedEvent.getParticipantLimit() != null) {
            eventToUpdate.setParticipantLimit(updatedEvent.getParticipantLimit());
        }
        if (updatedEvent.getRequestModeration() != null) {
            eventToUpdate.setRequestModeration(updatedEvent.getRequestModeration());
        }
        if (updatedEvent.getTitle() != null) {
            eventToUpdate.setTitle(updatedEvent.getTitle());
        }
        if (updatedEvent.getStateAction() != null) {
            if (eventToUpdate.getState().equals(StateEvent.PENDING)) {
                if (updatedEvent.getStateAction().equals(StateAction.REJECT_EVENT)) {
                    eventToUpdate.setState(StateEvent.CANCELED);
                }
                if (updatedEvent.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                    eventToUpdate.setState(PUBLISHED);
                    eventToUpdate.setPublished(LocalDateTime.now());
                }
            } else {
                throw new ValidationUpdateException("State of event do not have permission to update");
            }
        }
        if (updatedEvent.getLocation() != null) {
            Location location = locationRepository.save(LocationMapper.toEntity(updatedEvent.getLocation()));
            eventToUpdate.setLocation(location);
        }
        if (updatedEvent.getEventDate() != null && eventToUpdate.getState().equals(PUBLISHED)) {
            if (updatedEvent.getEventDate().isAfter(eventToUpdate.getPublished().plusHours(1))) {
                eventToUpdate.setEventDate(updatedEvent.getEventDate());
            } else {
                throw new ValidateDateException("Дата и время на которые намечено событие не может быть раньше, чем через час от текущего момента");
            }
        }
        repository.save(eventToUpdate);
        return EventMapper.toDto(eventToUpdate);
    }
}

