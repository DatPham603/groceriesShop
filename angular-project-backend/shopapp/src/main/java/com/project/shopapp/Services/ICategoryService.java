package com.project.shopapp.Services;

import com.project.shopapp.DTO.CategoryDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.Category;

import java.util.List;

public interface ICategoryService   {
    Category createCategory(CategoryDTO category);
    Category getCategoryById(long id) throws DataNotFoundException;
    List<Category> getAllCategories();
    List<Long> getAllCategoriesId();
    Category updateCategory(long id, CategoryDTO category) throws DataNotFoundException;
    void deleteCategory(long id);

}
