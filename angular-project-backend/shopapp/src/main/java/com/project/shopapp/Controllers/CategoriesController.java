package com.project.shopapp.Controllers;
import com.project.shopapp.DTO.CategoryDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.Category;
import com.project.shopapp.Responses.UpdateCategoriesResponse;
import com.project.shopapp.Services.CategoryService;
import com.project.shopapp.Services.ICategoryService;
import com.project.shopapp.Utils.LocalizationUtils;
import com.project.shopapp.Utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.List;
import java.util.Locale;

@RestController  
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final ICategoryService categoryService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("")
    public ResponseEntity<?> createCategories(@Valid @RequestBody CategoryDTO categoriesDTO, BindingResult result){
        if(result.hasErrors()){
            List<String> ErrorMessages = result.getFieldErrors()
                    .stream()
                    .map(fieldError -> fieldError.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(ErrorMessages);
        }
        categoryService.createCategory(categoriesDTO);
        return ResponseEntity.ok("This is insertCategories " + categoriesDTO);
    }
    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam("page") int page, @RequestParam("limit") int limit){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoriesResponse> updateCategories(@PathVariable long id,
                                                                     @RequestBody CategoryDTO categoryDTO,
                                                                     HttpServletRequest request) throws DataNotFoundException {
        categoryService.updateCategory(id,categoryDTO);
        return ResponseEntity.ok(UpdateCategoriesResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_SUCCESSFULLY))
                .build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategories(@PathVariable long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("This is deleteCategories with id = " + id + "successfully");
    }
}
