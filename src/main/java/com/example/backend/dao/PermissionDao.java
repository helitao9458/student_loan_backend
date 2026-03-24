package com.example.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionDao extends BaseMapper<Permission> {
    //根据用户id查找权限
    List<String> selectPermsByUserId(Long id);
}
