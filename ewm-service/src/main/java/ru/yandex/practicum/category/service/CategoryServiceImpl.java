package ru.yandex.practicum.category.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.category.dto.CategoryMapper;
import ru.yandex.practicum.category.repository.CategoryRepository;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.model.Category;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.event.repository.EventRepository;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.ValidationException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repo;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.toEntity(categoryDto);
        //проверка на уникальность имени в sql
        return CategoryMapper.toDto(repo.save(category));
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return CategoryMapper.toDto(repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория с Id=" + id + " не найдена!")));
    }

    @Override
    public void deleteCategoryById(Long id) {
        Category category = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория с Id=" + id + " не найдена!"));
        Collection<Event> event = eventRepository.findAllByCategoryId(id);
        boolean check = event.isEmpty();
        if (!check) {
            throw new ValidationException("Категория не может быть удалена, т.к. к ней привязаны события.");
        }
        repo.delete(category);
    }

    @Override
    public CategoryDto updateCategoryById(Long id, CategoryDto categoryDto) {
        Category category = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Категория с Id=" + id + " не найдена!"));
        category.setName(categoryDto.getName());
        return CategoryMapper.toDto(repo.save(category));
    }

    @Override
    public Collection<CategoryDto> getCategory(int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Category> list = repo.findAll(page).getContent();

        return list.stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

}
