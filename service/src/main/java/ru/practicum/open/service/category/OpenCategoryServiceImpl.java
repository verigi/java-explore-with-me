package ru.practicum.open.service.category;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.common.dto.category.CategoryDto;
import ru.practicum.common.dto.category.CreateCategoryDto;
import ru.practicum.common.mapper.CategoryMapper;
import ru.practicum.common.model.Category;
import ru.practicum.common.repository.CategoryRepository;
import ru.practicum.open.service.compilation.OpenCompilationServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class OpenCategoryServiceImpl implements OpenCategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public OpenCategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(Long catId) {
        log.debug("Attempting to fetch category. Id={}", catId);
        Optional<Category> categoryOptional = categoryRepository.findById(catId);
        if (!categoryOptional.isPresent()) {
            log.warn("Category not found. Id={}", catId);
            throw new EntityNotFoundException("Category not found. Id={}");
        } else {
            Category category = categoryOptional.get();
            return categoryMapper.toDto(category);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {
        log.debug("Attempting to fetch categories. From={}, size={}", from, size);
        Pageable pageable = PageRequest.of(from / size, size);

        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        return categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
}