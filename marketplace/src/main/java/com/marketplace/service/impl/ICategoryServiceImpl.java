package com.marketplace.service.impl;

import com.marketplace.dto.CategoryDto;
import com.marketplace.exception.ResourceNotFoundException;
import com.marketplace.mapper.CategoryMapper;
import com.marketplace.model.Category;
import com.marketplace.repository.CategoryRepository;
import com.marketplace.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ICategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);

        if (categoryDto.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryDto.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryDto.getParentCategoryId().toString()));
            category.setParentCategory(parentCategory);
            category.setLevel(parentCategory.getLevel() + 1);
        } else {
            category.setLevel(1);
        }

        category.setIsActive(true);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto setCategoryAsSubcategory(Long categoryId, Long parentCategoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId.toString()));
        Category parentCategory = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", parentCategoryId.toString()));

        category.setParentCategory(parentCategory);
        category.setLevel(parentCategory.getLevel() + 1);

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }



    @Override
    @Transactional
    public CategoryDto removeSubcategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId.toString()));

        // Check if the category has a parent
        if (category.getParentCategory() != null) {
            // Remove the category from its parent's subcategories
            category.setParentCategory(null);
            category.setLevel(1);  // Reset the level to 1 for the root category
            Category updatedCategory = categoryRepository.save(category);
            return categoryMapper.toDto(updatedCategory);
        } else {
            // If the category has no parent, just return it without any changes
            return categoryMapper.toDto(category);
        }
    }


    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id.toString()));
        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id.toString()));

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        if (categoryDto.getParentCategoryId() != null && !categoryDto.getParentCategoryId().equals(category.getParentCategory().getId())) {
            Category newParentCategory = categoryRepository.findById(categoryDto.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryDto.getParentCategoryId().toString()));
            category.setParentCategory(newParentCategory);
            category.setLevel(newParentCategory.getLevel() + 1);
        }

        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id.toString()));
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryDto> getRootCategories() {
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNull();
        return rootCategories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> getSubcategories(Long id) {
        Category parentCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id.toString()));
        List<Category> subcategories = categoryRepository.findByParentCategory(parentCategory);
        return subcategories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<String> getAllCategoriesNames() {
        List<Category> categories = categoryRepository.findAll();

        // Extract and return a simple list of all category names
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }



}