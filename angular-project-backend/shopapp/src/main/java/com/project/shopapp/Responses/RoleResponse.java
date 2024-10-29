package com.project.shopapp.Responses;

import com.project.shopapp.Models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponse {
    private String name;
    public static RoleResponse fromRole(Role role){
        return RoleResponse.builder().name(role.getName()).build();
    }
}
