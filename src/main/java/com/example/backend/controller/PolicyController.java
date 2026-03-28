package com.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.LoginUser;
import com.example.backend.pojo.Policy;
import com.example.backend.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/policy")
public class PolicyController {
    @Autowired
    PolicyService policyService;

    @PostMapping("/getallpolicy")
    public ResponseResult getAllPolicy(@RequestParam("page") Integer page, @RequestParam("policyType") Integer policyType, @RequestParam("title") String title){
        int size = 12;
        Page<Policy> policyPage = new Page<>(page, size);
        IPage<Policy> resultPage = policyService.findAllPolicies(policyPage,policyType,title);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    @PostMapping("/getallpolicys")
    public ResponseResult getAllPolicys(@RequestParam("page") Integer page, @RequestParam("policyType") Integer policyType, @RequestParam("title") String title){
        int size = 6;
        Page<Policy> policyPage = new Page<>(page, size);
        IPage<Policy> resultPage = policyService.findAllPolicie(policyPage,policyType,title);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    @PostMapping("/addpolicy")
    public ResponseResult addPolicy(@RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam(value = "img", required = false) MultipartFile img,  // 图片允许为空
                                    @RequestParam("type") Integer type) {
        String fileName = null;  // 初始化文件名为空
        if (img != null && !img.isEmpty()) {
            // 文件上传目录
            String uploadDir = "D:\\WRW\\Frontend\\vue-system\\src\\images";

            // 获取上传文件的原始文件名
            String originalFileName = img.getOriginalFilename();

            // 提取文件后缀名（如 .jpg, .png 等）
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // 生成唯一文件名，带上原始文件的后缀
            fileName = UUID.randomUUID().toString() + fileExtension;

            Path uploadPath = Paths.get(uploadDir);

            // 检查目录是否存在，不存在则创建
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);  // 创建文件夹
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseResult.error("文件夹创建失败");
                }
            }

            // 文件上传处理
            try (OutputStream outputStream = Files.newOutputStream(uploadPath.resolve(fileName))) {
                outputStream.write(img.getBytes());  // 写入文件
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseResult.error("文件上传失败");
            }
        }
        // 创建 Policy 对象并设置属性
        Policy policy = new Policy();
        policy.setTitle(title);
        policy.setContent(content);
        policy.setState(0);
        policy.setType(type);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUser().getId();
        policy.setCreatedBy(userid);

        // 如果上传了图片，设置图片文件名
        if (fileName != null) {
            policy.setImg(fileName);
        }

        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now().withNano(0);
        policy.setCreateTime(now);
        policy.setUpdateTime(now);

        // 保存 Policy 对象到数据库
        int result = policyService.addPolicy(policy);  // 假设有一个 service 层方法 `addPolicy`
        if (result > 0) {
            return ResponseResult.success("政策添加成功");
        } else {
            return ResponseResult.error("政策添加失败");
        }
    }

    @PostMapping("/updatestate")
    public ResponseResult updateState(@RequestParam("id")Long id,@RequestParam("state")Integer state){
        return policyService.updateState(id,state);
    }

    @PostMapping("/deletepolicy")
    public ResponseResult deleteById(@RequestParam("id")Long id){
        return policyService.deleteById(id);
    }

    @PostMapping("/updatePolicy")
    public ResponseResult updatePolicy(@RequestParam("id") Long id,
                                       @RequestParam("title") String title,
                                       @RequestParam("content") String content,
                                       @RequestParam(value = "img", required = false) MultipartFile img,  // 图片允许为空
                                       @RequestParam("type") Integer type) {
        // 1. 获取原来的政策对象
        Policy oldPolicy = policyService.selectById(id);

        // 2. 更新政策内容
        oldPolicy.setTitle(title);
        oldPolicy.setContent(content);
        oldPolicy.setType(type);
        oldPolicy.setUpdateTime(LocalDateTime.now());

        // 3. 如果有新图片上传
        String newImgName = null;
        if (img != null && !img.isEmpty()) {
            // 先删除旧的图片文件（如果有）
            String oldImg = oldPolicy.getImg();
            if (oldImg != null && !oldImg.isEmpty()) {
                Path oldImgPath = Paths.get("D:\\WRW\\Frontend\\vue-system\\src\\images", oldImg);
                try {
                    Files.deleteIfExists(oldImgPath);  // 删除旧图片
                } catch (IOException e) {
                    return ResponseResult.error("删除旧图片失败");
                }
            }

            // 保存新图片
            String originalFileName = img.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            newImgName = UUID.randomUUID().toString() + fileExtension;  // 使用UUID生成新的文件名

            Path uploadPath = Paths.get("D:\\WRW\\Frontend\\vue-system\\src\\images");
            if (!Files.exists(uploadPath)) {
                try {
                    Files.createDirectories(uploadPath);  // 如果目标文件夹不存在，则创建
                } catch (IOException e) {
                    return ResponseResult.error("创建目录失败");
                }
            }

            // 将图片写入到目标目录
            try (OutputStream outputStream = Files.newOutputStream(uploadPath.resolve(newImgName))) {
                outputStream.write(img.getBytes());
            } catch (IOException e) {
                return ResponseResult.error("文件上传失败");
            }

            // 更新政策对象中的图片名称
            oldPolicy.setImg(newImgName);
        }

        return policyService.updatePolicy(oldPolicy);
    }

}
