package com.project.shopapp.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "products_id")
    private Product product;
    @Column(name = "price")
    private Float price;
    @Column(name = "number_of_products")
    private int numberOfProducts;
    @Column(name = "total_money")
    private Float totalMoney;
    @Column(name = "color")
    private String color;
}
