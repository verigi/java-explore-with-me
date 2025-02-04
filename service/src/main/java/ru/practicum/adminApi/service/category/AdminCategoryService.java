package ru.practicum.adminApi.service.category;

import ru.practicum.general.dto.category.CategoryDto;
import ru.practicum.general.dto.category.CreateCategoryDto;
import ru.practicum.general.dto.category.UpdateCategoryDto;

public interface AdminCategoryService {
    CategoryDto createCategory(CreateCategoryDto createCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, UpdateCategoryDto updateCategoryDto);

}