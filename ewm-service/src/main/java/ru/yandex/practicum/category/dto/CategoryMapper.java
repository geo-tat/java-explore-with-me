package ru.yandex.practicum.category.dto;

import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.model.Category;

public class CategoryMapper {
    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toEntity(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
    }
}
