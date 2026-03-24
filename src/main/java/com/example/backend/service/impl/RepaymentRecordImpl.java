package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.dao.RepaymentRecordDao;
import com.example.backend.pojo.RepaymentRecord;
import com.example.backend.service.RepaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepaymentRecordImpl implements RepaymentRecordService {
    @Autowired
    private RepaymentRecordDao repaymentRecordDao;

    @Override
    public IPage<RepaymentRecord> getAll(Page<RepaymentRecord> repaymentRecordPage, Long userId) {
        QueryWrapper<RepaymentRecord> queryWrapper =new QueryWrapper<>();
        if(userId!=null&&userId!=0){
            queryWrapper.eq("user_id",userId);
        }
        IPage<RepaymentRecord> repaymentRecordIPage = repaymentRecordDao.selectPage(repaymentRecordPage,queryWrapper);
        return repaymentRecordIPage;
    }

    @Override
    public ResponseResult getAllRecord() {
        return ResponseResult.success(repaymentRecordDao.selectList(null));
    }
}
