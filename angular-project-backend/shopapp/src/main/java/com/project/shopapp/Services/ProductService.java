package com.project.shopapp.Services;

import com.project.shopapp.DTO.ProductDTO;
import com.project.shopapp.DTO.ProductImageDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Exception.InvalidParamException;
import com.project.shopapp.Models.Category;
import com.project.shopapp.Models.Product;
import com.project.shopapp.Models.ProductImage;
import com.project.shopapp.Repositories.CategoryRepository;
import com.project.shopapp.Repositories.ProductImageRepository;
import com.project.shopapp.Repositories.ProductRepository;
import com.project.shopapp.Responses.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository; // final khởi tạo 1 lần tham chiếu 1 lần mãi mãi
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    @Override
    public Product createProduct(ProductDTO productDTO) {
        Category categoryExisting;
        try {
            categoryExisting = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Can not find categories with id:" + productDTO.getCategoryId()));
        } catch (DataNotFoundException e) {
            throw new RuntimeException(e);
        }
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(categoryExisting)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws DataNotFoundException {
        return productRepository.getDetailProduct(productId)
                .orElseThrow(() -> new DataNotFoundException("Cant find product with id = " + productId));
    }
    //thieu get product theo categories

    @Override
    public Page<ProductResponse> getAllProduct(String keyword,long categoryId,PageRequest pageRequest) {
            return productRepository.findAll(categoryId,keyword,pageRequest)
                    .map(product -> ProductResponse.fromProduct(product));
    }

    @Override
    public List<Product> findProductsByProductIds(List<Long> productIds) {
        return productRepository.getProductsByIds(productIds);
    }

//    @Override
//    public Page<ProductResponse> getProductByCategoryId(PageRequest pageRequest, long categoryId) {
//        return productRepository.findByCategoryId(pageRequest,categoryId).map(product -> ProductResponse.fromProduct(product));
//    }

    @Override
    public Product updateProduct(long id, ProductDTO productDTO) throws DataNotFoundException {
        Product productExisting = this.getProductById(id);
        if(productExisting != null){
            Category categoryExisting = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Can not find categories with id:" + productDTO.getCategoryId()));
            productExisting.setName(productDTO.getName());
            productExisting.setPrice(productDTO.getPrice());
            productExisting.setThumbnail(productDTO.getThumbnail());
            productExisting.setDescription(productDTO.getDescription());
            productExisting.setCategory(categoryExisting);
            return productRepository.save(productExisting);
        }
        return null;
    }

    @Override
    public void deleteProducts(long id) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            productRepository.delete(product.get());
        }
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
    @Override
    public ProductImage createProductImage(ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException {
        Product existingProduct = productRepository.findById(productImageDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Can't not find product with id = " + productImageDTO.getProductId()));
        ProductImage productImage = ProductImage.builder()
                .imageUrl(productImageDTO.getImageUrl())
                .product(existingProduct)
                .build();
        int size = productImageRepository.findByProductId(existingProduct.getId()).size();
        if(size > 6){
            throw new InvalidParamException("Number of image must be < 6 ");
        }
        return productImageRepository.save(productImage);
    }
    public ResponseEntity<?> uploadImages(List<MultipartFile> files, Long productId) {
        try {
            Product productExisting = this.getProductById(productId);
            files = files == null ? new ArrayList<>() : files; // nếu null thì tạo danh sách file k thì trả về file
            if (files.size() > 6) {
                return ResponseEntity.badRequest().body("You can only upload maximum 6 images");
            }
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                // kiểm tra kích thước file và định dạng
                if (file.getSize() > 10 * 1024 * 1024) { // size > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File is too large!");
                }
                String contentType = file.getContentType(); // lấy định dạng file xem có phải ảnh không
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
                }
                String fileName = storeFile(file);
                ProductImage productImage = this.createProductImage(ProductImageDTO.builder()
                        .productId(productExisting.getId())
                        .imageUrl(fileName)
                        .build());
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    private boolean isImageFile(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    private String storeFile(MultipartFile file) throws IOException {
        if(!isImageFile(file) || file.getOriginalFilename() == null){
            throw new IOException("Invalid image file format");
        }
        //file.getOriginalFilename() Nó trả về tên gốc của tệp tải lên từ phía client.
        //StringUtils.cleanPath(String path): Đây là phương thức từ thư viện Apache Commons Lang
        // Nó làm sạch đường dẫn bằng cách loại bỏ các phần không hợp lệ hoặc dư thừa trong đường dẫn
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName; // tạo tên tệp không trùng
        java.nio.file.Path uploadDir = Paths.get("Uploads"); // thư mục lưu trữ
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir); // đ tồn tại thì tạo
        }
        //Paths.get : Tạo đối tượng Path cho tệp đích bằng cách kết hợp đường dẫn thư mục và tên tệp duy nhất.
        //Paths.get("/uploads", "file123.txt") sẽ tạo ra đối tượng Path đại diện cho đường dẫn "/uploads/file123.txt"
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFileName);
//        file.getInputStream(): Lấy InputStream từ đối tượng MultipartFile.
//        Files.copy(...): Sao chép nội dung của InputStream vào tệp đích.
//        StandardCopyOption.REPLACE_EXISTING: Tùy chọn sao chép để thay thế tệp đích nếu nó đã tồn tại.
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }
}
