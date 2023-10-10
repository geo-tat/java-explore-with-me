package ru.practicum.user;

public class UserMapper {
    public static User toEntity(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto toUserDto (User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toUserShortDto(User initiator) {
        return UserShortDto.builder()
                .id(initiator.getId())
                .name(initiator.getName())
                .build();
    }
}
