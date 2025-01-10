package ru.practicum.common.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.category.CreateCategoryDto;
import ru.practicum.common.dto.category.UpdateCategoryDto;
import ru.practicum.common.model.Category;

@Component
public class CategoryMapper {
    public CategoryDto toDto(Category category) {
        return category == null ? null : CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category toEntity(CreateCategoryDto createCategoryDto) {
        return createCategoryDto == null ? null : Category.builder()
                .name(createCategoryDto.getName())
                .build();
    }

    public Category updateEntity(Category category, UpdateCategoryDto updateCategoryDto) {
        if (category == null || updateCategoryDto == null) {
            return null;
        }
        category.setName(updateCategoryDto.getName());
        return category;
    }
}