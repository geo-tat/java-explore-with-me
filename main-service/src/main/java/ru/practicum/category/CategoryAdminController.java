package ru.practicum.category;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {
   private final CategoryService service;

    @PostMapping
   public CategoryDto addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return service.saveCategory(categoryDto);
    }
    @DeleteMapping("/{catId}")
    public void deleteCategory(@PathVariable Long catId) {
        service.deleteCategoryById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId, @Valid @RequestBody CategoryDto categoryDto) {

        return service.updateCategoryById(catId,categoryDto);

    }



}
