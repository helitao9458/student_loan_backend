package com.example.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.pojo.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleDao extends BaseMapper<Role> {
    Long selectByRoleName(String RoleName);
    String findRoleByUserId(Long id);
    //根据用户编号查询角色名称
    String selectRoleById(Long id);
}
