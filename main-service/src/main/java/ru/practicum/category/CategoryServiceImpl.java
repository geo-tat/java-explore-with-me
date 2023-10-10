package ru.practicum.category;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.EntityNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repo;

    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
       Category category = CategoryMapper.toEntity(categoryDto);
       //проверка на уникальность имени в sql
        return CategoryMapper.toDto(repo.save(category));
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return CategoryMapper.toDto(repo.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Категория с Id=" + id + " не найдена!")));
    }

    @Override
    public boolean deleteCategoryById(Long id) {
        Category category = repo.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Категория с Id=" + id + " не найдена!"));
        repo.delete(category);
        return true;
    }

    @Override
    public CategoryDto updateCategoryById(Long id, CategoryDto categoryDto) {
        Category category = repo.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Категория с Id=" + id + " не найдена!"));
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
