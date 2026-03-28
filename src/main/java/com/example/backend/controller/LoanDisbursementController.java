package com.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.LoanApplication;
import com.example.backend.pojo.LoanDisbursement;
import com.example.backend.service.LoanDisbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/disbursement")
public class LoanDisbursementController {
    @Autowired
    private LoanDisbursementService loanDisbursementService;

    @PostMapping("/getllDisbursement")
    public ResponseResult getAllDisbursement(@RequestParam("page")Integer page,@RequestParam(value = "loanApplicationId", required = false) Long loanApplicationId
    ){
        int size = 12;
        Page<LoanDisbursement> disbursementPage = new Page<>(page, size);
        IPage<LoanDisbursement> resultPage = loanDisbursementService.getAll(disbursementPage,loanApplicationId);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

}
