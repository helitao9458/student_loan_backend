package com.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.LoanDisbursement;
import com.example.backend.pojo.RepaymentSchedule;
import com.example.backend.service.RepaymentScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/schedule")
public class RepaymentScheduleController {
    @Autowired
    private RepaymentScheduleService repaymentScheduleService;

    @PostMapping("/getschedule")
    public ResponseResult getSchedule(){
        return repaymentScheduleService.getSchedule();
    }

    @PostMapping("/paymoney")
    public ResponseResult payMoney(@RequestParam("id")Long id,@RequestParam("amount")Integer amount,@RequestParam("type")String type,@RequestParam("loanApplicationId")Long loanApplicationId){
        return repaymentScheduleService.updateStatus(id,amount,type,loanApplicationId);
    }

    @PostMapping("/getAllSchedule")
    public ResponseResult getAll(@RequestParam("page")Integer page,@RequestParam("status")Integer status){
        int size = 12;
        Page<RepaymentSchedule> repaymentSchedulePage = new Page<>(page, size);
        IPage<RepaymentSchedule> resultPage = repaymentScheduleService.getAll(repaymentSchedulePage,status);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    //通知还款功能
    @PostMapping("/sendMsg")
    public ResponseResult sendMsg(@RequestParam("id")Long id,@RequestParam("loanApplicationId")Long loanApplicationId){
        return repaymentScheduleService.sendMsg(id,loanApplicationId);
    }
}
