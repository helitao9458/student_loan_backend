package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.dao.LoanReviewDao;
import com.example.backend.pojo.LoanReview;
import com.example.backend.service.LoanReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanReviewImpl implements LoanReviewService {
    @Autowired
    private LoanReviewDao loanReviewDao;

    @Override
    public IPage<LoanReview> findAllLoanReviews(Page<LoanReview> loanReviewPage, Integer reviewStatus) {
        QueryWrapper<LoanReview> queryWrapper = new QueryWrapper<>();
        if(reviewStatus!=-1){
            queryWrapper.eq("review_status",reviewStatus);
        }
        IPage<LoanReview> loanReviewIPage = loanReviewDao.selectPage(loanReviewPage,queryWrapper);
        return loanReviewIPage;
    }

    @Override
    public ResponseResult getReason(Long loanApplicationId) {
        QueryWrapper<LoanReview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("loan_application_id",loanApplicationId);
        LoanReview loanReview = loanReviewDao.selectOne(queryWrapper);
        if(loanReview!=null){
            return ResponseResult.success(loanReview.getReviewComments());
        }else {
            return ResponseResult.error("没有记录");
        }

    }
}
