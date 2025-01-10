package ru.practicum.admin.service.category;

import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.category.CreateCategoryDto;
import ru.practicum.common.dto.category.UpdateCategoryDto;

public interface AdminCategoryService {
    CategoryDto createCategory(CreateCategoryDto createCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, UpdateCategoryDto updateCategoryDto);

}