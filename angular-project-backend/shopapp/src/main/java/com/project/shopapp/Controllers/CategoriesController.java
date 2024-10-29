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

@RestController  //phương thức trong lớp này sẽ xử lý các yêu cầu HTTP và trả về các phản hồi dưới dạng dữ liệu JSON/XML
//@Validated được sử dụng ở mức class để đánh dấu class này cần validate
@RequestMapping("api/v1/categories")//http://localhost:8088/api/v1/categories?page=10&limit=20
@RequiredArgsConstructor
public class CategoriesController {
    private final ICategoryService categoryService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("")//tạo mới
    //nếu tham số truyền vào là một đối tượng thì thì nó được gọi là : Data Transfer Object (DTO) = Request Object
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
    @GetMapping("") //@RequestParam Truyền các thông tin bổ sung, không bắt buộc vd products?category=electronics&sort=price
    public ResponseEntity<List<Category>> getAllCategories(@RequestParam("page") int page, @RequestParam("limit") int limit){
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    @PutMapping("/{id}")//đã tồn tại
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
