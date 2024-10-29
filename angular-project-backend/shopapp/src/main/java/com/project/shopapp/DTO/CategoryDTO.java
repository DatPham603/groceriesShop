package com.project.shopapp.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data //TƯƠNG TỰ TOSTRING
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotEmpty(message = "Category's name can't empty")
    private String name;
}
