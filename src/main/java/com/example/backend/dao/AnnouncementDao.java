package com.example.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.pojo.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AnnouncementDao extends BaseMapper<Announcement> {
    // 查询文章及其对应评论的分页查询
    IPage<Announcement> findAllAnnouncementsWithComments(Page<Announcement> page, @Param("title") String title);

    IPage<Announcement> findAllAnnouncementsWithComment(Page<Announcement> announcementPage, @Param("title") String title);
}
