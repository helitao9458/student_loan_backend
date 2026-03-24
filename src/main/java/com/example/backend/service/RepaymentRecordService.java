package com.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.RepaymentRecord;

public interface RepaymentRecordService {
    IPage<RepaymentRecord> getAll(Page<RepaymentRecord> repaymentRecordPage, Long userId);

    ResponseResult getAllRecord();
}
