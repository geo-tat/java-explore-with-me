package ru.practicum.event.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryDto;
import ru.practicum.event.StateEvent;
import ru.practicum.location.Location;

import ru.practicum.user.UserDto;
import ru.practicum.user.UserShortDto;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    Long id;

    String title;

    String annotation;
    CategoryDto category;

    Integer confirmedRequest;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;

    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    UserShortDto initiator;

    Location location;

    Boolean paid;

    Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime published;

    Boolean requestModeration;

    StateEvent state;

    Long views;

}
