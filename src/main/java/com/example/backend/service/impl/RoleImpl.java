package com.example.backend.service.impl;

import com.example.backend.common.ResponseResult;
import com.example.backend.dao.RoleDao;
import com.example.backend.pojo.Role;
import com.example.backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RoleImpl implements RoleService {
    @Autowired
    private RoleDao roleDao;

    @Override
    public ResponseResult selectRoleById(Long id) {
        String role = roleDao.selectRoleById(id);
        if(role!=null){
            return new ResponseResult(200,"成功",role);
        }else {
            return new ResponseResult(500,"失败","null");
        }
    }

    @Override
    public ResponseResult getAll() {
        return ResponseResult.success(roleDao.selectList(null));
    }

    @Override
    public ResponseResult addRole(String roleName, String roleDescription) {
        Role role = new Role();
        role.setRoleName(roleName);
        role.setDescription(roleDescription);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        if(roleDao.insert(role)>0){
            return ResponseResult.success("添加角色成功");
        }else {
            return ResponseResult.success("添加角色失败");
        }
    }
}
