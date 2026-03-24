package com.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.LoanApplication;

public interface LoanApplicationService {
    ResponseResult save(LoanApplication loanApplication);

    IPage<LoanApplication> findAllPolicies(Page<LoanApplication> loanPage, String applicant, Integer status,String school);

    ResponseResult getAllLoans();

    ResponseResult updateStatus(Long id, Integer status, String reviewComments);

    IPage<LoanApplication> getTrue(Page<LoanApplication> loanPage, String applicant, String school);

    ResponseResult distribute(Long id);

    IPage<LoanApplication> getBySelf(Page<LoanApplication> loanPage, Integer status);
}
