package com.example.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.RepaymentRecord;
import com.example.backend.pojo.RepaymentSchedule;
import com.example.backend.service.RepaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/record")
public class RepaymentRecordController {
    @Autowired
    private RepaymentRecordService repaymentRecordService;

    @PostMapping("/getAllRecords")
    public ResponseResult getAllRecords(@RequestParam("page")Integer page,@RequestParam(value = "userId",required = false)Long userId){
        int size = 12;
        Page<RepaymentRecord> repaymentRecordPage = new Page<>(page, size);
        IPage<RepaymentRecord> resultPage = repaymentRecordService.getAll(repaymentRecordPage,userId);
        Map<String, Object> response = new HashMap<>();
        response.put("leaveList", resultPage.getRecords());
        response.put("totalPages", resultPage.getPages());
        response.put("totalElements", resultPage.getTotal());
        return new ResponseResult(200,response);
    }

    //获取数据用来展示图表
    @PostMapping("/getAllRecord")
    public ResponseResult getAllRecord(){
        return repaymentRecordService.getAllRecord();
    }
}
