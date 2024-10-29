package com.project.shopapp.Services;

import com.project.shopapp.DTO.RoleDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.Role;
import com.project.shopapp.Repositories.RoleRepository;
import com.project.shopapp.Responses.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService{
    private final RoleRepository roleRepository;
    @Override
    public RoleResponse createRole(RoleDTO roleDTO) {
        Role role = Role.builder().name(roleDTO.getName()).build();
        roleRepository.save(role);
        return RoleResponse.fromRole(role);
    }

    @Override
    public RoleResponse updateRole(Long roleId, RoleDTO roleDTO) throws DataNotFoundException {
        Role existingRole =  roleRepository.findById(roleId)
                .orElseThrow(() -> new DataNotFoundException("Cant find role with id = " + roleId));
        existingRole.setName(roleDTO.getName());
        roleRepository.save(existingRole);
        return RoleResponse.fromRole(existingRole);
    }

    @Override
    public RoleResponse getRoleById(Long roleId) throws DataNotFoundException {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new DataNotFoundException("Cant find role with id = " + roleId));
        return RoleResponse.fromRole(role);
    }

    @Override
    public void deleteRoleById(Long roleId) throws DataNotFoundException {
        Role existingRole = roleRepository.findById(roleId).orElse(null);
        if(existingRole == null){
            throw new DataNotFoundException("Cant find any role with id = "+ roleId);
        }else {
            roleRepository.deleteById(roleId);
        }
    }
}
