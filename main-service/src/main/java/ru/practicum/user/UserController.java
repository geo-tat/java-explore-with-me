package ru.practicum.user;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return service.add(userDto);
    }

    @DeleteMapping("/{id}")
    Boolean deleteUser(@Positive @PathVariable Long id) {
        return service.delete(id);
    }

    @GetMapping
    Collection<UserDto> getUsers(@RequestParam int[] ids,
                                 @RequestParam(defaultValue = "10") @Positive Integer size,
                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from) {
        return service.getAll(ids, from, size);
    }
}
