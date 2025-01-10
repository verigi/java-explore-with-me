package ru.practicum.admin.service.category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.category.CreateCategoryDto;
import ru.practicum.common.dto.category.UpdateCategoryDto;
import ru.practicum.common.exceptions.DuplicationException;
import ru.practicum.common.mapper.CategoryMapper;
import ru.practicum.common.model.Category;
import ru.practicum.common.repository.CategoryRepository;

import java.util.Optional;

@Slf4j
@Repository
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public AdminCategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        log.debug("Attempting to create category. Name={}", createCategoryDto.getName());
        if (categoryRepository.findByName(createCategoryDto.getName()).isPresent()) {
            log.warn("Category with name \"{}\" already exists", createCategoryDto.getName());
            throw new DuplicationException("Category with name " + createCategoryDto.getName() + " already exists");
        }
        Category category = categoryMapper.toEntity(createCategoryDto);
        Category savedCategory = categoryRepository.save(category);

        log.debug("Category successfully created: name={}",
                savedCategory.getName());
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public void deleteCategory(Long catId) {
        log.debug("Attempting to delete category. Id={}", catId);
        Optional<Category> categoryOptional = categoryRepository.findById(catId);
        if (!categoryOptional.isPresent()) {
            log.warn("Category with id {} does not exists", catId);
            throw new EntityNotFoundException("Category with id " + catId + " does not exists");
        } else {
            Category category = categoryOptional.get();
            categoryRepository.delete(category);

            log.debug("Category successfully deleted: name={}",
                    category.getName());
        }
    }

    @Override
    public CategoryDto updateCategory(Long catId, UpdateCategoryDto updateCategoryDto) {
        log.debug("Attempting to update category. Id={}", catId);
        Optional<Category> categoryOptional = categoryRepository.findById(catId);
        if (!categoryOptional.isPresent()) {
            log.warn("Category with id {} does not exists", catId);
            throw new EntityNotFoundException("Category with id " + catId + " does not exists");
        } else {
            if (categoryRepository.findByName(updateCategoryDto.getName()).isPresent()) {
                log.warn("Category with name \"{}\" exists already");
                throw new DuplicationException("Category with name " + updateCategoryDto.getName() + " exists already");
            }

            Category category = categoryMapper.updateEntity(categoryOptional.get(), updateCategoryDto);
            categoryRepository.save(category);

            log.debug("Category successfully updated: name={}",
                    category.getName());
            return categoryMapper.toDto(category);
        }
    }
}