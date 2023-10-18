package ru.yandex.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {

    private Long id;
    @NotBlank(message = "Отсутствует имя пользователя")
    @Size(min = 2, max = 250)
    private String name;
    @NotBlank(message = "Отсутствует почтовый адрес")
    @Email(message = "Адрес введен с ошибкой")
    @Size(min = 6, max = 254)
    private String email;


}
