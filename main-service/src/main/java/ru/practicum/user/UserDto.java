package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {

    private Long id;
    @NotBlank(message = "Отсутствует имя пользователя")
    private String name;
    @NotBlank(message = "Отсутствует почтовый адрес")
    @Email(message = "Адрес введен с ошибкой")
    private String email;


}
