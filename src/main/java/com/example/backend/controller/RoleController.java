package com.example.backend.controller;

import com.example.backend.common.ResponseResult;
import com.example.backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("/selectByid")
    public ResponseResult selectRoleById(@RequestParam("id")Long id){
        return roleService.selectRoleById(id);
    }

    @PostMapping("/getAllRole")
    public ResponseResult getAllRole(){
        return roleService.getAll();
    }

    @PostMapping("/addrole")
    public ResponseResult addRole(@RequestParam("roleName")String roleName,@RequestParam("roleDescription")String roleDescription){
        return roleService.addRole(roleName,roleDescription);
    }
}
