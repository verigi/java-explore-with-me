package ru.practicum.open.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.open.service.category.OpenCategoryService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class OpenCategoryController {
    private final OpenCategoryService service;

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable("catId") Long catId) {
        log.debug("New GET request received. Category id: {}", catId);
        CategoryDto categoryDto = service.getCategory(catId);
        log.debug("Category id={} successfully found", catId);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDto);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        log.debug("New GET request received. Get all categories");
        List<CategoryDto> categoryDtos = service.getCategories(from, size);
        if (categoryDtos.isEmpty()) {
            log.debug("No categories fetched. Return empty list");
        } else {
            log.debug("Categories successfully fetched. Count: {}", categoryDtos.size());
        }
        return ResponseEntity.status(HttpStatus.OK).body(categoryDtos);
    }

}