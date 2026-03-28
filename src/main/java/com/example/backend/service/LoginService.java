package com.example.backend.service;

import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.User;


public interface LoginService {
    ResponseResult login(User user);
    ResponseResult logout();
    ResponseResult addUser(User user);
}
