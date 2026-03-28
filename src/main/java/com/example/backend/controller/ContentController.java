package com.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.Content;
import com.example.backend.pojo.LoanApplication;
import com.example.backend.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/content")
public class ContentController {
    @Autowired
    private ContentService contentService;

    @PostMapping("/addcontent")
    public ResponseResult addContent(@RequestParam("announcementId")Long announcementId,@RequestParam("content")String content){
        return contentService.addContent(announcementId,content);
    }

    @PostMapping("/getallcontent")
    public ResponseResult getAllContent(@RequestParam("page")Integer page,@RequestParam("content")String content){
        int size = 8;
        Page<Content> contentPage = new Page<>(page, size);
        IPage<Content> resultPage = contentService.findContents(contentPage,content);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    @PostMapping("/deleteContent")
    public ResponseResult deleteContent(@RequestParam("id")Long id){
        return contentService.deleteById(id);
    }
}
