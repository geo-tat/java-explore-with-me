package ru.yandex.practicum.category.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/categories")
@Validated
public class CategoryPublicController {
    CategoryService service;

    @GetMapping
    Collection<CategoryDto> getAll(@RequestParam(value = "from", defaultValue = "0")
                                   @PositiveOrZero Integer from,
                                   @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return service.getCategory(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto getById(@PathVariable Long catId) {
        return service.getCategoryById(catId);
    }
}
