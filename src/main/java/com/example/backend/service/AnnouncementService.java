package com.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.Announcement;

public interface AnnouncementService {
    IPage<Announcement> findAllAnnouncements(Page<Announcement> announcementPage, String title);

    ResponseResult save(Announcement announcement);

    ResponseResult updateStatus(Long id, Integer status);

    IPage<Announcement> findAll(Page<Announcement> announcementPage, String title);

    ResponseResult updateAnnouncement(Announcement announcement);

    ResponseResult deleteById(Long id);
}
