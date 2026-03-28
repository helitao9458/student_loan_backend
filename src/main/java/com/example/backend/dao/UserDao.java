package com.example.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao extends BaseMapper<User> {
    // 根据 id 更新 state 字段
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    // 根据 idCard和email找回密码
    int updateByIdCard(@Param("idCard") String idCard, @Param("email") String email, @Param("password") String password);
    //根据id修改密码
    int updatePassword(@Param("userId") Long userId,@Param("encryptedNewPassword") String encryptedNewPassword);

    int updateByIds(User oldUser);
}
