package ru.yandex.practicum.category.service;

import ru.yandex.practicum.category.dto.CategoryDto;

import java.util.Collection;

public interface CategoryService {

    CategoryDto saveCategory(CategoryDto categoryDto);

    void deleteCategoryById(Long id);

    CategoryDto updateCategoryById(Long id, CategoryDto categoryDto);

    Collection<CategoryDto> getCategory(int from, int size);

    CategoryDto getCategoryById(Long catId);
}
