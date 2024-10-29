package com.project.shopapp.Services;

import com.project.shopapp.DTO.CategoryDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.Category;
import com.project.shopapp.Repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{
    private final CategoryRepository categoryRepository;
    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(long id) throws DataNotFoundException {
        return categoryRepository.findById(id).orElseThrow(() -> new DataNotFoundException("category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Long> getAllCategoriesId() {
        return categoryRepository.findAll().stream().map((category) -> category.getId()).toList();
    }

    @Override
    public Category updateCategory(long categoryId, CategoryDTO categoryDTO) throws DataNotFoundException {
        Category categoryExisting = this.getCategoryById(categoryId);
        categoryExisting.setName(categoryDTO.getName());
        return categoryRepository.save(categoryExisting);
    }
    // xóa cứng , xóa mất luôn
    @Override
    public void deleteCategory(long id) {
        categoryRepository.deleteById(id);
    }
}
