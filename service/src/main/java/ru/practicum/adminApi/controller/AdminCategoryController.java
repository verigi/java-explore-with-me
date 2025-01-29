package ru.practicum.adminApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.adminApi.service.category.AdminCategoryService;
import ru.practicum.general.dto.category.CategoryDto;
import ru.practicum.general.dto.category.CreateCategoryDto;
import ru.practicum.general.dto.category.UpdateCategoryDto;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private final AdminCategoryService service;

    @PostMapping
    public ResponseEntity<CategoryDto> saveCategory(@Valid @RequestBody CreateCategoryDto createCategoryDto) {
        log.debug("New POST request received. Category name: {}", createCategoryDto.getName());
        CategoryDto categoryDto = service.createCategory(createCategoryDto);
        log.debug("Category \"{}\" successfully saved", categoryDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDto);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("catId") Long catId) {
        log.debug("New DELETE request received. Category id: {}", catId);
        service.deleteCategory(catId);
        log.debug("Category with id={} successfully deleted", catId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable("catId") Long catId,
                                              @Valid @RequestBody UpdateCategoryDto updateCategoryDto) {
        log.debug("New PATCH request received. Category id: {}, new category name: {}", catId, updateCategoryDto.getName());
        CategoryDto categoryDto = service.updateCategory(catId, updateCategoryDto); // тут будет сервис
        log.debug("Category \"{}\" successfully updated", categoryDto.getId());
        return ResponseEntity.status(HttpStatus.OK).body(categoryDto);
    }
}