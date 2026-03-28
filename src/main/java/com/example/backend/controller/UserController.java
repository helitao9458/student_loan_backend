package com.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.User;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 更换用户角色
    @PostMapping("/changeRole")
    public ResponseResult changeRole(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId) {
        return userService.changeRole(userId, roleId); // 调用 service 层更改角色
    }

    @PostMapping("/getalluser")
    public ResponseResult getAllUser(@RequestParam("page") Integer page, @RequestParam("realName") String realName, @RequestParam("idCard") String idCard){
        int size = 4;
        Page<User> userPage = new Page<>(page, size);
        IPage<User> resultPage = userService.findAllUser(userPage,realName,idCard);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    //修改角色状态
    @PostMapping("/updatestate")
    public ResponseResult updateState(@RequestParam("id")Long id,@RequestParam("status")Integer status){
        return userService.updateStatus(id,status);
    }

    //修改角色信息
    @PostMapping("/updateuser")
    public ResponseResult updateUser(@RequestParam("realName") String realName,
                                     @RequestParam("id") Long id,
                                     @RequestParam(value = "major", required = false) String major,
                                     @RequestParam(value = "avatar", required = false) MultipartFile avatar,  // 图片允许为空
                                     @RequestParam(value = "school", required = false) String school,
                                     @RequestParam("idCard") String idCard,
                                     @RequestParam("email") String email,
                                     @RequestParam(value = "bankName", required = false) String bankName,
                                     @RequestParam("phoneNumber") String phoneNumber){
        User oldUser = userService.selectById(id);
        String uploadDir = "D:\\WRW\\Frontend\\vue-system\\src\\images";
        // 如果上传了新头像文件
        if (avatar != null && !avatar.isEmpty()) {
            try {
                // 删除旧头像
                if (oldUser.getAvatar() != null) {
                    File oldAvatarFile = new File(uploadDir, oldUser.getAvatar());
                    if (oldAvatarFile.exists()) {
                        oldAvatarFile.delete(); // 删除旧头像文件
                    }
                }
                // 获取上传文件的原始文件名
                String originalFileName = avatar.getOriginalFilename();

                // 提取文件后缀名（如 .jpg, .png 等）
                String fileExtension = "";
                if (originalFileName != null && originalFileName.contains(".")) {
                    fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                }
                // 保存新头像
                String originalFilename = avatar.getOriginalFilename();
                String newFileName = UUID.randomUUID().toString() + fileExtension;
                File newAvatarFile = new File(uploadDir, newFileName);
                avatar.transferTo(newAvatarFile); // 保存新头像文件

                // 更新用户头像路径
                oldUser.setAvatar(newFileName);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseResult.error("头像上传失败");
            }
        }
        oldUser.setRealName(realName);
        oldUser.setMajor(major);
        oldUser.setSchool(school);
        oldUser.setIdCard(idCard);
        oldUser.setEmail(email);
        oldUser.setBankName(bankName);
        oldUser.setPhoneNumber(phoneNumber);
        oldUser.setUpdatedAt(LocalDateTime.now());
        return userService.updateUser(oldUser);
    }

    //根据身份证和邮箱来查找密码
    @PostMapping("/findpwd")
    public ResponseResult reFindPassword(@RequestParam("idCard")String idCard,@RequestParam("email")String email){
        return userService.sendPassword(idCard,email);
    }

    //修改密码
    @PostMapping("/updatepwd")
    public ResponseResult updatePwd(@RequestParam("newPwd")String newPwd,@RequestParam("oldPwd")String oldPwd){
        return userService.updatePwd(newPwd,oldPwd);
    }

    //获取正在登录的用户信息
    @PostMapping("/getmy")
    public ResponseResult getBySelf(){
        return userService.getMySelf();
    }

    //根据用户id查询用户名称
    @PostMapping("/getNameById")
    public ResponseResult getNameById(@RequestParam("id")Long id){
        return userService.getNameById(id);
    }

    //可以根据账号查询用户
    @PostMapping("/getchatuser")
    public ResponseResult getChatUser(@RequestParam("page") Integer page,@RequestParam("userName") String userName){
        int size = 8;
        Page<User> userPage = new Page<>(page, size);
        IPage<User> resultPage = userService.findAllChatUser(userPage,userName);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    @PostMapping("/getAllusers")
    public ResponseResult getAllUsers(){
        return userService.getAllUsers();
    }
}
