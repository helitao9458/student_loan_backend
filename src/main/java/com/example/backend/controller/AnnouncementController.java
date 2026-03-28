package com.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.Announcement;
import com.example.backend.pojo.LoanApplication;
import com.example.backend.pojo.LoginUser;
import com.example.backend.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    @Autowired
    private AnnouncementService announcementService;


//    @PreAuthorize("hasAuthority('test')")  使用@PreAuthorize来进行权限控制
    @PostMapping("/getAllAnnouncement")
    public ResponseResult getAllAnnouncement(@RequestParam("page") Integer page,@RequestParam("title")String title){
        int size = 8;
        Page<Announcement> announcementPage = new Page<>(page, size);
        IPage<Announcement> resultPage = announcementService.findAllAnnouncements(announcementPage,title);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    @PostMapping("/getAllAnnouncements")
    public ResponseResult getAllAnnouncements(@RequestParam("page") Integer page,@RequestParam("title")String title){
        int size = 12;
        Page<Announcement> announcementPage = new Page<>(page, size);
        IPage<Announcement> resultPage = announcementService.findAll(announcementPage,title);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    @PostMapping("/updateStatus")
    public ResponseResult updateStatus(@RequestParam("id")Long id,@RequestParam("status")Integer status){
        return announcementService.updateStatus(id,status);
    }

    @PostMapping("/addAnnouncement")
    public ResponseResult addAnnouncement(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("images") List<MultipartFile> images) {

        // Prepare image storage paths
        String uploadDir = "D:/WRW/Frontend/vue-system/src/images";
        List<String> imagePaths = images.stream().map(file -> {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("文件名字为空");
            }

            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

            String uuidFileName = UUID.randomUUID().toString() + fileExtension;

            try {
                Files.copy(file.getInputStream(), Paths.get(uploadDir, uuidFileName));
                return uuidFileName;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("存储文件失败" + originalFilename);
            }
        }).collect(Collectors.toList());

        String imgs = String.join(",", imagePaths);

        //获取当前正在进行请求的用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Announcement announcement = new Announcement();
        announcement.setImg(imgs);
        announcement.setBank(loginUser.getUser().getBankName());
        announcement.setContent(content);
        announcement.setTitle(title);
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        announcement.setStatus(1);
        return announcementService.save(announcement);
    }

    @PostMapping("/updateAnnouncement")
    public ResponseResult updateAnnouncement(@RequestBody Announcement announcement){
        return announcementService.updateAnnouncement(announcement);
    }

    @PostMapping("/deleteAnnouncement")
    public ResponseResult deleteAnnouncement(@RequestParam("id")Long id){
        return announcementService.deleteById(id);
    }
}
