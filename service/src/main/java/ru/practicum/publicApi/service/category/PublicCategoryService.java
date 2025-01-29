package ru.practicum.publicApi.service.category;

import ru.practicum.general.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoryService {
    CategoryDto getCategory(Long catId);

    List<CategoryDto> getCategories(int from, int size);
}