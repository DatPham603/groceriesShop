package com.project.shopapp.Controllers;

import com.github.javafaker.Faker;
import com.project.shopapp.DTO.ProductDTO;
import com.project.shopapp.DTO.ProductImageDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.Product;
import com.project.shopapp.Models.ProductImage;
import com.project.shopapp.Models.User;
import com.project.shopapp.Responses.ProductListResponse;
import com.project.shopapp.Responses.ProductResponse;
import com.project.shopapp.Services.ICategoryService;
import com.project.shopapp.Services.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductsController {
    private final IProductService productService;
    private final ICategoryService categoryService;
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(@RequestParam("page") int page,
                                                           @RequestParam("limit") int limit,
                                                           @RequestParam(defaultValue = "0", name = "categoryId") Long categoryId,
                                                           @RequestParam(defaultValue = "") String keyword) {
        PageRequest pageRequest = PageRequest.of(page,limit,//Sort.by("createdAt").descending()
        Sort.by("id").ascending());
        Page<ProductResponse> productPage = productService.getAllProduct(keyword,categoryId,pageRequest);
        int totalPage = productPage.getTotalPages();//tổng số trang mà bạn có trong tập dữ liệu
        List<ProductResponse> productList = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse.builder()
                        .productResponses(productList)
                        .totalPage(totalPage)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductsById(@PathVariable("id") Long productId) throws DataNotFoundException {
        Product productExisting = productService.getProductById(productId);
        return ResponseEntity.ok(productExisting);
    }
    @GetMapping("/byIds")
    public ResponseEntity<?> getProductsByProductIds(@RequestParam("ids") String ids){
        try{
            List<Long> productIds = Arrays.stream(ids.split(",")).map(p -> Long.parseLong(p)).toList();
            return ResponseEntity.ok(productService.findProductsByProductIds(productIds)) ;
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @GetMapping("category/{id}")
//    public ResponseEntity<ProductListResponse> getProductsByCategoryId(@PathVariable("id") Long categoryId,
//                                                                       @RequestParam("page") int page,
//                                                                       @RequestParam("limit") int limit){
//        PageRequest pageRequest = PageRequest.of(page,limit,//Sort.by("createdAt").descending()
//                Sort.by("id").ascending());
//        Page<ProductResponse> productResponsesPage = productService.getProductByCategoryId(pageRequest,categoryId);
//        List<ProductResponse> productList = productResponsesPage.getContent();
//        return ResponseEntity.ok(ProductListResponse.builder().productResponses(productList).build());
//    }
    //value ~ ""
    //MIME (Multipurpose Internet Mail Extensions) type
    // là một tiêu chuẩn được sử dụng để chỉ định loại dữ liệu được truyền qua Internet: text/html, application/json, image/png,multipart/form-data
    //consumes : Khi bạn muốn chỉ định các loại MIME mà phương thức có thể tiêu thụ (nhận) từ phần thân yêu cầu
    @PostMapping("")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO,
                                           //@RequestPart("file") MultipartFile file,//Để chỉ định rằng một phần cụ thể của yêu cầu multipart sẽ được ánh xạ vào một tham số trong phương thức của bạn
                                           //Đối tượng MultipartFile chứa các thông tin và dữ liệu của tệp tin tải lên.
                                           //@RequestPart("file"): Ánh xạ phần của yêu cầu multipart với tên là "file" vào đối tượng MultipartFile có tên là file
                                           BindingResult result) { //BindingResult là bắt lỗi từ đối tượng @Valid ?
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError -> FieldError.getDefaultMessage())
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok().body(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long productId,@RequestBody ProductDTO productDTO) throws DataNotFoundException {
        return ResponseEntity.ok().body(productService.updateProduct(productId,productDTO));
    }
    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //onsumes cho biết kiểu nội dung mà phương thức này sẽ xử lý.
    public ResponseEntity<?> upLoadImages(@ModelAttribute("files") List<MultipartFile> files,@PathVariable("id") Long productId) {
        return productService.uploadImages(files, productId);
    }
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try{
            Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource urlResource = new UrlResource(imagePath.toUri());
            if(urlResource.exists()){
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(urlResource);
            }else {
                return ResponseEntity.notFound().build();
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving image:" + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") long id) {
        return ResponseEntity.status(HttpStatus.OK).body("Product delete successfully with id :" + id);
    }
    @PostMapping("/generateFakeProduct")
    public ResponseEntity<String> generateFakeProduct(){
        Faker faker = new Faker();
        List<Long> categorieIds = categoryService.getAllCategoriesId();
        for(int i = 0 ; i < 1000; i++){
            Long randomCategoriesId = categorieIds.get(faker.number().numberBetween(0,categorieIds.size()));
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)){
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10,500))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId(randomCategoriesId)
                    .build();
            productService.createProduct(productDTO);
        }
        return ResponseEntity.ok("Fake product was created");
    }

}
