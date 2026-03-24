package com.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.LoanReview;
import com.example.backend.service.LoanReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/loanreview")
public class LoanReviewController {
    @Autowired
    private LoanReviewService loanReviewService;

    @PostMapping("/getAllReview")
    public ResponseResult getAllReview(@RequestParam("page")Integer page,@RequestParam("reviewStatus")Integer reviewStatus){
        int size = 10;
        Page<LoanReview> loanReviewPage = new Page<>(page, size);
        IPage<LoanReview> resultPage = loanReviewService.findAllLoanReviews(loanReviewPage,reviewStatus);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    @PostMapping("/getreason")
    public ResponseResult getReason(@RequestParam("loanApplicationId")Long loanApplicationId){
        return loanReviewService.getReason(loanApplicationId);
    }
}
