package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.dao.LoanDisbursementDao;
import com.example.backend.pojo.LoanApplication;
import com.example.backend.pojo.LoanDisbursement;
import com.example.backend.service.LoanDisbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoanDisbursementImpl implements LoanDisbursementService {
    @Autowired
    private LoanDisbursementDao loanDisbursementDao;


    @Override
    public IPage<LoanDisbursement> getAll(Page<LoanDisbursement> disbursementPage, Long loanApplicationId) {
        // 构建查询条件
        QueryWrapper<LoanDisbursement> queryWrapper = new QueryWrapper<>();
        // 添加政策类型的查询条件（如果有提供）
        if (!StringUtils.isEmpty(loanApplicationId)){
            queryWrapper.eq("loan_application_id",loanApplicationId);
        }
        queryWrapper.orderByDesc("disbursement_time");
        // 执行分页查询
        IPage<LoanDisbursement> disbursementIPage = loanDisbursementDao.selectPage(disbursementPage, queryWrapper);
        return disbursementIPage;
    }
}
