package com.project.shopapp.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.DTO.CartItemDTO;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse{
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("full_name")
    private String fullName;
    private String email;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String address;
    private String note;
    @JsonProperty("order_date")
    private LocalDateTime orderDate;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("status")
    private String status;
    @JsonProperty("shipping_date")
    private LocalDate shippingDate;
    @JsonProperty("shipping_method")
    private String shippingMethod;
    @JsonProperty("total_money")
    private Float totalMoney;
    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;
}
