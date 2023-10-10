package ru.practicum.user;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto add(UserDto userDto) {
        User userToSave = UserMapper.toEntity(userDto);
        return UserMapper.toUserDto(repository.save(userToSave));
    }

    @Override
    public Boolean delete(Long userId) {
        User usertoDelete = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found."));
        repository.delete(usertoDelete);
        return true;
    }

    @Override
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID=" + userId + " not found.")));
    }

    @Override
    public Collection<UserDto> getAll(int[] ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Long> idsToRep = new ArrayList<>();
        Collection<User> users = new ArrayList<>();
        if(ids == null) {
            return repository.findAll(page).getContent().stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        for (int id : ids) {
            idsToRep.add((long) id);
        }
        return repository.findAllByIdIn(idsToRep, page).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
