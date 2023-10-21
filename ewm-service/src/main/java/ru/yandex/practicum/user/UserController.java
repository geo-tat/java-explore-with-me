package ru.yandex.practicum.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/users")
@Validated
public class UserController {
    UserService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return service.add(userDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    Boolean deleteUser(@Positive @PathVariable Long id) {
        return service.delete(id);
    }

    @GetMapping
    Collection<UserDto> getUsers(@RequestParam(required = false) int[] ids,
                                 @RequestParam(defaultValue = "10") @Positive Integer size,
                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from) {
        return service.getAll(ids, from, size);
    }
}
