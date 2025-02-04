package ru.practicum.adminApi.service.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.general.dto.category.CategoryDto;
import ru.practicum.general.dto.category.CreateCategoryDto;
import ru.practicum.general.dto.category.UpdateCategoryDto;
import ru.practicum.general.exceptions.DuplicationException;
import ru.practicum.general.mapper.CategoryMapper;
import ru.practicum.general.model.Category;
import ru.practicum.general.repository.CategoryRepository;
import ru.practicum.general.util.ValidationHandler;

import java.util.Optional;

@Slf4j
@Repository
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ValidationHandler validationHandler;

    @Autowired
    public AdminCategoryServiceImpl(CategoryRepository categoryRepository,
                                    CategoryMapper categoryMapper,
                                    ValidationHandler validationHandler) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.validationHandler = validationHandler;
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        log.debug("Attempting to create category: {}", createCategoryDto.getName());

        validationHandler.validateCategoryName(createCategoryDto.getName());
        Category category = categoryMapper.toEntity(createCategoryDto);
        Category savedCategory = categoryRepository.save(category);

        log.debug("Category created: {}", savedCategory.getName());
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.debug("Attempting to delete category: {}", catId);

        Category category = validationHandler.findEntityById(categoryRepository, catId, "Category");
        validationHandler.validateRelatedEvents(catId);
        categoryRepository.delete(category);

        log.debug("Category deleted: {}", category.getName());
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, UpdateCategoryDto updateCategoryDto) {
        log.debug("Attempting to update category: {}", catId);

        Category category = validationHandler.findEntityById(categoryRepository, catId, "Category");

        Optional<Category> existingCategory = categoryRepository.findByName(updateCategoryDto.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(catId)) {
            throw new DuplicationException("Category with name " + updateCategoryDto.getName() + " already exists");
        }

        category = categoryMapper.updateEntity(category, updateCategoryDto);
        categoryRepository.save(category);

        log.debug("Category updated: {}", category.getName());
        return categoryMapper.toDto(category);
    }


}