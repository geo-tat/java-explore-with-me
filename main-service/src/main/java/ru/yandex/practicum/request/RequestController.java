package ru.yandex.practicum.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.request.service.RequestService;

import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService service;

    @GetMapping
    public Collection<ParticipationRequestDto> getParticipationRequest(@PathVariable Long userId) {

        return service.getRequestsById(userId);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ParticipationRequestDto saveParticipationRequest(@PathVariable Long userId,
                                                            @RequestParam Long eventId) {
        return service.saveRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto updateRequestToCancel(
            @PathVariable(value = "userId") Long userId,
            @PathVariable(value = "requestId") Long requestId) {
        return service.updateToCancel(userId, requestId);
    }
}
