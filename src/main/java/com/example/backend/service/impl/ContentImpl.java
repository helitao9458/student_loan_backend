package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.dao.ContentDao;
import com.example.backend.pojo.Content;
import com.example.backend.pojo.LoginUser;
import com.example.backend.service.ContentService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class ContentImpl implements ContentService {
    @Autowired
    private ContentDao contentDao;

    @Override
    public ResponseResult addContent(Long announcementId, String content) {
        Content content1 = new Content();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        content1.setUserId(loginUser.getUser().getId());
        content1.setAnnouncementId(announcementId);
        content1.setContent(content);
        content1.setCreateTime(LocalDateTime.now());
        if(contentDao.insert(content1)>0){
            return ResponseResult.success("评论成功");
        }else {
            return ResponseResult.error("评论失败");
        }
    }

    @Override
    public IPage<Content> findContents(Page<Content> contentPage, String content) {
        QueryWrapper<Content> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(content)){
            queryWrapper.like("content",content);
        }
        IPage<Content> contentIPage = contentDao.selectPage(contentPage,queryWrapper);
        return contentIPage;
    }

    @Override
    public ResponseResult deleteById(Long id) {
        // 尝试删除评论
        int result = contentDao.deleteById(id);

        // 判断删除是否成功
        if (result > 0) {
            return ResponseResult.success("删除成功");
        } else {
            return ResponseResult.error("删除失败，评论不存在或已被删除");
        }
    }
}
