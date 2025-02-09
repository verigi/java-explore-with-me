package ru.practicum.adminApi.service.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.general.dto.category.CategoryDto;
import ru.practicum.general.dto.category.CreateCategoryDto;
import ru.practicum.general.dto.category.UpdateCategoryDto;
import ru.practicum.general.exceptions.DuplicationException;
import ru.practicum.general.mapper.CategoryMapper;
import ru.practicum.general.model.Category;
import ru.practicum.general.repository.CategoryRepository;
import ru.practicum.general.util.EntityHandler;

import java.util.Optional;

@Slf4j
@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EntityHandler entityHandler;

    @Autowired
    public AdminCategoryServiceImpl(CategoryRepository categoryRepository,
                                    CategoryMapper categoryMapper,
                                    EntityHandler entityHandler) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.entityHandler = entityHandler;
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        log.debug("Attempting to create category={}", createCategoryDto.getName());

        entityHandler.validateCategoryName(createCategoryDto.getName());
        Category category = categoryMapper.toEntity(createCategoryDto);
        categoryRepository.save(category);

        log.debug("Category created. Id={}", category.getId());
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        log.debug("Attempting to delete category={}", catId);

        Category category = entityHandler.findEntityById(categoryRepository, catId, "Category");
        entityHandler.validateRelatedEvents(catId);
        categoryRepository.delete(category);

        log.debug("Category deleted. Id={}", catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, UpdateCategoryDto updateCategoryDto) {
        log.debug("Attempting to update category={}", catId);

        Category category = entityHandler.findEntityById(categoryRepository, catId, "Category");
        Optional<Category> existingCategory = categoryRepository.findByName(updateCategoryDto.getName());
        if (existingCategory.isPresent() && !existingCategory.get().getId().equals(catId)) {
            throw new DuplicationException("Category with name " + updateCategoryDto.getName() + " already exists");
        }

        category = categoryMapper.updateEntity(category, updateCategoryDto);
        categoryRepository.flush();

        log.debug("Category updated. Id={}", catId);
        return categoryMapper.toDto(category);
    }


}