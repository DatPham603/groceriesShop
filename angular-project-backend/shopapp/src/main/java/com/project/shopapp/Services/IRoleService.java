package com.project.shopapp.Services;

import com.project.shopapp.DTO.RoleDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.Role;
import com.project.shopapp.Responses.RoleResponse;

public interface IRoleService {
    RoleResponse createRole(RoleDTO roleDTO);
    RoleResponse updateRole(Long roleId,RoleDTO roleDTO) throws DataNotFoundException;
    RoleResponse getRoleById(Long roleId) throws DataNotFoundException;
    void deleteRoleById(Long roleId) throws DataNotFoundException;
}
