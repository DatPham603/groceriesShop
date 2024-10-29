package com.project.shopapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.Models.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDTO {
    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's ID must be > 1")
    private Long productId;
    @JsonProperty("image_url")
    @Size(min = 5, max = 200, message = "Image url")
    private String imageUrl;
}
