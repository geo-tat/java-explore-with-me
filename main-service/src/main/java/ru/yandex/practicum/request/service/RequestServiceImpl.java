package ru.yandex.practicum.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.model.StateEvent;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.ValidationException;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.yandex.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.dto.RequestMapper;
import ru.yandex.practicum.request.model.EventRequestStatus;
import ru.yandex.practicum.request.model.Request;
import ru.yandex.practicum.request.repository.RequestRepository;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.model.User;
import ru.yandex.practicum.user.repository.UserRepository;
import ru.yandex.practicum.user.service.UserService;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final RequestRepository repository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<ParticipationRequestDto> getRequestsById(Long userId) {
        UserDto user = userService.getById(userId);
        return repository.findAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto saveRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + eventId + " not found!"));
        // нельзя добавить повторный запрос (Ожидается код ошибки 409)   перехватывается в бд
        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии");
        }
        if (event.getConfirmedRequests().equals(event.getParticipantLimit()) && event.getParticipantLimit() != 0) {
            throw new ValidationException("У события достигнут лимит запросов на участие");
        }

        Request newRequest = Request.builder()
                .requester(user)
                .event(event)
                .build();

        if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
            newRequest.setState(EventRequestStatus.PENDING);
        } else {
            newRequest.setState(EventRequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return RequestMapper.toDto(repository.save(newRequest));
    }


    @Override
    public ParticipationRequestDto updateToCancel(Long userId, Long requestId) {
        UserDto user = userService.getById(userId);
        Request requestToUpdate = repository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request with id=" + requestId + " not found"));
        if (requestToUpdate.getRequester().getId().equals(userId)) {
            requestToUpdate.setState(EventRequestStatus.CANCELED);
        } else {
            throw new ValidationException("У пользователя с id=" + userId + " нет доступа к запросу");
        }
        return RequestMapper.toDto(repository.save(requestToUpdate));
    }

    @Override
    public Collection<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + eventId + " not found!"));
        if (event.getInitiator().getId().equals(userId)) {
            return repository.findAllByEventId(eventId)
                    .stream()
                    .map(RequestMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatus(Long userId,
                                                                   Long eventId,
                                                                   EventRequestStatusUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event id=" + eventId + " not found!"));

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ValidationException("Если для события лимит заявок равен 0 или отключена пре-модерация заявок," +
                    " то подтверждение заявок не требуется");
        }
        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ValidationException("Лимит подтвержденных запросов достигнут");
        }
        List<Request> requests = repository.findAllByIdIn(request.getRequestIds());
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        switch (request.getStatus()) {
            case CONFIRMED:
                int confirmationLimit = event.getParticipantLimit() - event.getConfirmedRequests();
                for (Request request1 : requests) {
                    if (!request1.getState().equals(EventRequestStatus.PENDING)) {
                        throw new ValidationException("Cтатус можно изменить только у заявок, находящихся в состоянии ожидания");
                    }
                    if (confirmationLimit != 0) {
                        request1.setState(EventRequestStatus.CONFIRMED);
                        confirmationLimit--;
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    } else {
                        request1.setState(EventRequestStatus.REJECTED);
                    }
                }
                break;
            case REJECTED:
                requests.forEach(request1 -> request1.setState(EventRequestStatus.REJECTED));
                repository.saveAll(requests);
        }
        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        for (Request request1 : requests) {
            if (request1.getState().equals(EventRequestStatus.CONFIRMED)) {
                confirmed.add(RequestMapper.toDto(request1));
            } else if (request1.getState().equals(EventRequestStatus.REJECTED)) {
                rejected.add(RequestMapper.toDto(request1));
            }
        }
        result.setRejectedRequests(rejected);
        result.setConfirmedRequests(confirmed);
        eventRepository.save(event);
        return result;
    }
}
