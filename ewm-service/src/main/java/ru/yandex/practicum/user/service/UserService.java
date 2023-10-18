package ru.yandex.practicum.user.service;


import ru.yandex.practicum.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto add(UserDto userDto);

    Boolean delete(Long userId);

    UserDto getById(Long userId);

    Collection<UserDto> getAll(int[] ids, Integer from, Integer size);

}
