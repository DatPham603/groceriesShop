package com.project.shopapp.Repositories;

import com.project.shopapp.Models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);
    Page<Product> findByCategoryId(Pageable pageable, long categoryId);
    @Query("SELECT p FROM Product p WHERE (:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId)" +
            "AND (  :keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword% )")
    Page<Product> findAll(@Param("categoryId") Long categoryId,@Param("keyword") String keyword, Pageable pageable);
    //Sử dụng LEFT JOIN FETCH để lấy luôn thông tin của productImageList (danh sách các ảnh) liên kết với Product.
    //LEFT JOIN: Trả về tất cả các record từ bảng bên trái (table1) và mọi record phù hợp từ bảng bên phải (table2).
    // Nếu không khớp, giá trị NULL sẽ được hiển thị cho các cột của bảng bên phải (table2).
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.productImageList WHERE p.id = :productId")
    Optional<Product> getDetailProduct(@Param("productId") Long productId);
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> getProductsByIds(@Param("productIds") List<Long> productIds);
}
