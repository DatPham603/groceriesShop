package com.project.shopapp.Controllers;

import com.project.shopapp.DTO.RoleDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.Role;
import com.project.shopapp.Services.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/role")
public class RoleController {
    private final IRoleService roleService;
    @PostMapping("")
    public ResponseEntity<?> createRole(@RequestBody RoleDTO roleDTO){
        return ResponseEntity.ok().body(roleService.createRole(roleDTO));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getRole(@PathVariable("id") Long roleId) throws DataNotFoundException {
        return ResponseEntity.ok().body(roleService.getRoleById(roleId));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable("id") Long id, @RequestBody RoleDTO roleDTO) throws DataNotFoundException {
        return ResponseEntity.ok().body(roleService.updateRole(id,roleDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoleById(@PathVariable("id") Long roleId) throws DataNotFoundException {
        roleService.deleteRoleById(roleId);
        return ResponseEntity.ok().body("Delete role suceesfull");
    }
}
