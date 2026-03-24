package com.example.backend.service.impl;

import com.example.backend.common.ResponseResult;
import com.example.backend.dao.RoleDao;
import com.example.backend.dao.UserDao;
import com.example.backend.dao.UserRoleDao;
import com.example.backend.pojo.LoginUser;
import com.example.backend.pojo.User;
import com.example.backend.service.LoginService;
import com.example.backend.tools.JwtUtil;
import com.example.backend.tools.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private PasswordEncoder passwordEncoder; // 自动注入 PasswordEncoder

    @Override
    public ResponseResult login(User user) {
        //AuthenticationManager authenticate进行用户认证
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //如果认证没通过，给出对应的提示（如果authenticate为null，则认证不通过）
        if (Objects.isNull(authenticate)) {
            return new ResponseResult(500, "账号密码错误");
        }
        //如果认证通过了，使用userId生成一个jwt，jwt存入ResponseResult返回
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        String role = roleDao.findRoleByUserId(loginUser.getUser().getId());
        Map<String,String> map = new HashMap<>();
        map.put("token", jwt);
        map.put("role",role);
        //把完整的用户信息存入redis，userId作为key
        redisCache.setCacheObject("login:"+userId, loginUser);
        return new ResponseResult(200, "登录成功", map);
    }

    @Override
    public ResponseResult logout() {
        //获取当前正在进行请求的用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUser().getId();
        redisCache.deleteObject("login:"+userid);
        return new ResponseResult(200,"退出成功");
    }

    @Override
    public ResponseResult addUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setStatus(0);

        // 1. 对用户密码进行加密
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 2. 添加用户到 User 表中
        userDao.insert(user);

        // 检查是否成功获取到用户 ID
        if (user.getId() == null) {
            return new ResponseResult(500, "用户添加失败，未获取到用户ID");
        }

        // 3. 找到学生对应的角色 ID
        Long roleId = roleDao.selectByRoleName("student");
        if (roleId == null) {
            return new ResponseResult(500, "学生角色未找到");
        }

        // 4. 关联用户与角色
        int result = userRoleDao.insertUserRole(user.getId(), roleId);
        if (result <= 0) {
            return new ResponseResult(500, "用户角色关联失败");
        }

        // 5. 返回添加成功的响应
        return new ResponseResult(200, "用户添加成功");
    }


}
