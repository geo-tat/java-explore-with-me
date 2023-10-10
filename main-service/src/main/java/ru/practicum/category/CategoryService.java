package ru.practicum.category;

import java.util.Collection;

public interface CategoryService {

    CategoryDto saveCategory(CategoryDto categoryDto);

    boolean deleteCategoryById(Long id);

    CategoryDto updateCategoryById(Long id, CategoryDto categoryDto);

    Collection<CategoryDto> getCategory(int from, int size);

    CategoryDto getCategoryById(Long catId);
}
