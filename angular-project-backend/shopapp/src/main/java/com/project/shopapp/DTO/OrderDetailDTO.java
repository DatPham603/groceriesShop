package com.project.shopapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "orderId must be >= 1")
    private Long orderId;
    @JsonProperty("products_id")
    @Min(value = 1, message = "productId must be >= 1")
    private Long productId;
    @Min(value = 0, message = "price must be > 0")
    private Float price;
    @JsonProperty("number_of_products")
    @Min(value = 1, message = "number of product must be > 1")
    private int numberOfProducts;
    @Min(value = 0, message = "total money must be > 0")
    @JsonProperty("total_money")
    private Float totalMoney;
    private String color;
}
