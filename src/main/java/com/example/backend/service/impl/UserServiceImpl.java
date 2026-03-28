package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.dao.UserDao;
import com.example.backend.dao.UserRoleDao;
import com.example.backend.pojo.LoginUser;
import com.example.backend.pojo.Policy;
import com.example.backend.pojo.User;
import com.example.backend.service.UserService;
import com.example.backend.tools.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailUtil emailUtil;

    @Override
    public IPage<User> findAllUser(Page<User> userPage, String realName, String idCard) {
        // 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 添加用户的身份证查询条件（如果有提供）
        if (!StringUtils.isEmpty(idCard)) { // 如果没有类别时默认查询所有
            queryWrapper.eq("id_card", idCard);
        }
        // 添加标题模糊查询的条件（如果有提供）
        if (!StringUtils.isEmpty(realName)) {
            queryWrapper.like("real_name", realName);
        }
        // 添加状态的条件，假设 state=0 表示正常用户
//        queryWrapper.eq("state", 0);
        queryWrapper.orderByDesc("created_at");
        // 执行分页查询
        IPage<User> userIPage = userDao.selectPage(userPage, queryWrapper);
        return userIPage;
    }

    @Override
    public ResponseResult updateStatus(Long id, Integer status) {
        User user = userDao.selectById(id);
        if(status==0){
            EmailUtil.sendCustomEmail(user.getEmail(),"国家助学贷款系统","你的账号为："+user.getUserName()+"已解封");
        }else if(status==1){
            EmailUtil.sendCustomEmail(user.getEmail(),"国家助学贷款系统","你的账号为："+user.getUserName()+"已被封号");
        }
        // 更新状态并获取结果
        int rowsAffected = userDao.updateStatus(id,status);
        if (rowsAffected > 0) {
            return ResponseResult.success("状态更新成功");
        } else {
            return ResponseResult.error("状态更新失败");
        }
    }

    @Override
    public User selectById(Long id) {
        return userDao.selectById(id);
    }

    @Override
    public ResponseResult updateUser(User oldUser) {
        // 更新用户信息
        userDao.updateByIds(oldUser);

        // 更新 SecurityContext 中的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            // 假设 LoginUser 也有一个更新方法，或者直接替换其 User 信息
            loginUser.setUser(userDao.selectById(oldUser.getId()));  // 更新用户信息

            // 创建新的 Authentication 对象并设置回 SecurityContext
            Authentication newAuth = new UsernamePasswordAuthenticationToken(loginUser, authentication.getCredentials(), authentication.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        return ResponseResult.success("成功");
    }


    @Override
    public ResponseResult sendPassword(String idCard, String email) {
        // 生成新的 8 位随机密码
        String newPassword = emailUtil.generateRandomPassword();

        // 使用 BCryptPasswordEncoder 对生成的密码进行加密
        String password = passwordEncoder.encode(newPassword);

        // 根据 idCard 和 email 更新加密后的密码
        int result = userDao.updateByIdCard(idCard, email, password);

        // 根据更新结果进行判断
        if (result > 0) {
            // 发送未加密的随机密码到用户邮箱
            emailUtil.sendAuthCodeEmail(email, newPassword);
            return ResponseResult.success("新密码已发送到您的邮箱");
        } else {
            return ResponseResult.error("更新密码失败，用户信息不匹配");
        }
    }

    @Override
    public ResponseResult updatePwd(String newPwd, String oldPwd) {
        // 获取当前登录的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();
        // 验证旧密码是否正确
        String currentPassword = loginUser.getUser().getPassword();  // 数据库中的加密旧密码
        if (!passwordEncoder.matches(oldPwd, currentPassword)) {
            return ResponseResult.error("旧密码不正确");
        }
        // 对新密码进行加密
        String encryptedNewPassword = passwordEncoder.encode(newPwd);
        // 更新用户密码
        int updateResult = userDao.updatePassword(userId, encryptedNewPassword);
        if (updateResult > 0) {
            loginUser.setUser(userDao.selectById(userId));
            // 创建新的 Authentication 对象并设置回 SecurityContext
            Authentication newAuth = new UsernamePasswordAuthenticationToken(loginUser, authentication.getCredentials(), authentication.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            return ResponseResult.success("密码更新成功");
        } else {
            return ResponseResult.error("密码更新失败，请稍后重试");
        }
    }

    @Override
    public ResponseResult getMySelf() {
        // 获取当前登录的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        return ResponseResult.success(userDao.selectById(loginUser.getUser().getId()));
    }

    @Override
    public ResponseResult changeRole(Long userId, Long roleId) {
        userRoleDao.updateRole(userId,roleId);
        return null;
    }

    @Override
    public ResponseResult getNameById(Long id) {
        String realName = userDao.selectById(id).getRealName();
        return ResponseResult.success(realName);
    }

    @Override
    public IPage<User> findAllChatUser(Page<User> userPage, String userName) {
        // 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 添加账号模糊查询的条件（如果有提供）
        if (!StringUtils.isEmpty(userName)) {
            queryWrapper.like("user_name", userName);
        }
        // 添加状态的条件，假设 status=0 表示正常用户
        queryWrapper.eq("status", 0);
        queryWrapper.orderByDesc("created_at");
        // 执行分页查询
        IPage<User> userIPage = userDao.selectPage(userPage, queryWrapper);
        return userIPage;
    }

    @Override
    public ResponseResult getAllUsers() {
        return ResponseResult.success(userDao.selectList(null));
    }

}
