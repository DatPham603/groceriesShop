package com.project.shopapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginDTO {
    @JsonProperty("phone_number")
    @NotBlank(message = "Phonenumber is required")
    private String phoneNumber;
    @NotBlank(message = "Password is required")
    private String password;
}