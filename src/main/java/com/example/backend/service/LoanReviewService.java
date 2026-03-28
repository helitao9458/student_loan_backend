package com.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.LoanReview;

public interface LoanReviewService {
    IPage<LoanReview> findAllLoanReviews(Page<LoanReview> loanReviewPage, Integer reviewStatus);

    ResponseResult getReason(Long loanApplicationId);
}
