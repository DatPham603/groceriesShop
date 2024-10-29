package com.project.shopapp.Models;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
//MappedSuperclass Được sử dụng bởi JPA, chỉ định rằng lớp này không phải là một thực thể,
// nhưng các thực thể con kế thừa từ nó sẽ bao gồm các trường và phương thức của lớp này.
public class BaseEntity {
    @Column(name = "created_at")
    private LocalDateTime createdAt; // LocalDateTime lưu cả ngày tháng năm giờ phút giây
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    //@PrePersist:Phương thức onCreate() được đánh dấu với @PrePersist sẽ
    // được gọi tự động ngay trước khi một thực thể mới được lưu vào cơ sở dữ liệu (lệnh insert)
    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    //@PreUpdate: Phương thức onUpdate() được đánh dấu với @PreUpdate sẽ được gọi tự động
    // ngay trước khi một thực thể hiện tại được cập nhật trong cơ sở dữ liệu ( lệnh update)
    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
