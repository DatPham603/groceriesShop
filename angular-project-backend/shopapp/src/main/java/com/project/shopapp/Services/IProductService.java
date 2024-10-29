package com.project.shopapp.Services;

import com.project.shopapp.DTO.ProductDTO;
import com.project.shopapp.DTO.ProductImageDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Exception.InvalidParamException;
import com.project.shopapp.Models.Product;
import com.project.shopapp.Models.ProductImage;
import com.project.shopapp.Responses.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    Product createProduct(ProductDTO productDTO);
    Product getProductById(long id) throws DataNotFoundException;
    Page<ProductResponse> getAllProduct(String keyword,long categoryId,PageRequest pageRequest);
    //Page<ProductResponse> getProductByCategoryId(PageRequest pageRequest,long categoryId);
    List<Product> findProductsByProductIds(List<Long> productIds);
    Product updateProduct(long id, ProductDTO productDTO) throws DataNotFoundException;
    void deleteProducts(long id);
    boolean existsByName(String name);
    ResponseEntity<?> uploadImages(List<MultipartFile> files, Long productId);
    ProductImage createProductImage( ProductImageDTO productImageDTO)
            throws DataNotFoundException, InvalidParamException;
}
