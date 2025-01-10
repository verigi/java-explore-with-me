package ru.practicum.open.service.category;

import ru.practicum.common.dto.category.CategoryDto;

import java.util.List;

public interface OpenCategoryService {
    CategoryDto getCategory(Long catId);

    List<CategoryDto> getCategories(int from, int size);
}