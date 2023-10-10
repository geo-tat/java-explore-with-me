package ru.practicum.user;


import java.util.Collection;

public interface UserService {

UserDto add(UserDto userDto);
Boolean delete(Long userId);

UserDto getById(Long userId);

Collection<UserDto> getAll(int[] ids, Integer from, Integer size);



}
