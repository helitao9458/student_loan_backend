package com.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.LoanDisbursement;

public interface LoanDisbursementService {
    IPage<LoanDisbursement> getAll(Page<LoanDisbursement> disbursementPage, Long loanApplicationId);
}
