package com.project.shopapp.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
//@Table(name = "categories") rất quan trọng trong việc ánh xạ lớp Java với bảng trong cơ sở dữ liệu.
// Nếu không có @Table, JPA sẽ mặc định sử dụng tên của lớp để xác định tên bảng trong cơ sở dữ liệu.
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
}
