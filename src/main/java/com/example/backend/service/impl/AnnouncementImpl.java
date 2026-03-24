package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.dao.AnnouncementDao;
import com.example.backend.dao.ContentDao;
import com.example.backend.pojo.Announcement;
import com.example.backend.pojo.Content;
import com.example.backend.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementImpl implements AnnouncementService {
    @Autowired
    private AnnouncementDao announcementDao;
    @Autowired
    private ContentDao contentDao;

    // 用户分页查询所有文章及评论
    @Override
    public IPage<Announcement> findAllAnnouncements(Page<Announcement> announcementPage, String title) {
        return announcementDao.findAllAnnouncementsWithComments(announcementPage, title);
    }

    @Override
    public ResponseResult save(Announcement announcement) {
        if(announcementDao.insert(announcement)>0){
            return ResponseResult.success("添加成功");
        }else {
            return ResponseResult.error("添加失败");
        }
    }

    @Override
    public ResponseResult updateStatus(Long id, Integer status) {
        // 构建查询条件
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);  // 根据 id 条件查询

        // 创建 Announcement 对象并设置状态
        Announcement announcement = new Announcement();
        announcement.setStatus(status);  // 设置新的状态

        // 执行更新操作
        int rows = announcementDao.update(announcement, queryWrapper);

        // 判断更新是否成功，返回响应结果
        if (rows > 0) {
            return ResponseResult.success("状态更新成功");
        } else {
            return ResponseResult.error("状态更新失败");
        }
    }

    @Override
    public IPage<Announcement> findAll(Page<Announcement> announcementPage, String title) {
        return announcementDao.findAllAnnouncementsWithComment(announcementPage, title);
    }

    @Override
    public ResponseResult updateAnnouncement(Announcement announcement) {
        // 创建 UpdateWrapper 用于指定更新条件和字段
        UpdateWrapper<Announcement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", announcement.getId()) // 条件：根据 id 更新
                .set("title", announcement.getTitle())  // 更新 title 字段
                .set("content", announcement.getContent()); // 更新 content 字段

        // 调用 MyBatis Plus 的 update 方法执行更新
        int result = announcementDao.update(null, updateWrapper);

        if (result > 0) {
            return ResponseResult.success("公告更新成功");
        } else {
            return ResponseResult.error("公告更新失败");
        }
    }

    @Override
    public ResponseResult deleteById(Long id) {
        // 1. 删除文章
        announcementDao.deleteById(id);

        // 2. 查询与文章关联的评论
        QueryWrapper<Content> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("announcement_id", id);
        List<Content> list = contentDao.selectList(queryWrapper);

        // 3. 提取评论ID列表
        List<Long> commentIds = list.stream().map(Content::getId).collect(Collectors.toList());

        // 4. 删除所有关联的评论
        if (!commentIds.isEmpty()) {
            contentDao.deleteBatchIds(commentIds);
        }

        return ResponseResult.success("文章及评论删除成功");
    }
}
