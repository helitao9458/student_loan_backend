package com.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.User;

public interface UserService {
    IPage<User> findAllUser(Page<User> userPage, String realName, String idCard);
    ResponseResult updateStatus(Long id, Integer status);
    User selectById(Long id);
    ResponseResult updateUser(User oldUser);
    ResponseResult sendPassword(String idCard, String email);
    ResponseResult updatePwd(String newPwd, String oldPwd);
    ResponseResult getMySelf();

    ResponseResult changeRole(Long userId, Long roleId);
    //根据用户编号查询名称
    ResponseResult getNameById(Long id);

    IPage<User> findAllChatUser(Page<User> userPage, String userName);
    //查询所有用户数据在前端进行分析
    ResponseResult getAllUsers();
}
