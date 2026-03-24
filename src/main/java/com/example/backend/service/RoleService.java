package com.example.backend.service;

import com.example.backend.common.ResponseResult;

public interface RoleService {
    //根据用户编号查询角色信息
    ResponseResult selectRoleById(Long id);

    ResponseResult getAll();

    ResponseResult addRole(String roleName, String roleDescription);
}
