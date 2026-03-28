package com.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.Content;

public interface ContentService {
    ResponseResult addContent(Long announcementId, String content);

    IPage<Content> findContents(Page<Content> contentPage, String content);

    ResponseResult deleteById(Long id);
}
