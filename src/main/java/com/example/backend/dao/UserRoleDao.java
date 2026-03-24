package com.example.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleDao {
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    void updateRole(@Param("userId") Long userId,@Param("roleId") Long roleId);
}

