package com.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.RepaymentSchedule;

public interface RepaymentScheduleService {
    ResponseResult getSchedule();

    ResponseResult updateStatus(Long id, Integer amount, String type,Long loanApplicationId);

    IPage<RepaymentSchedule> getAll(Page<RepaymentSchedule> repaymentSchedulePage, Integer status);

    ResponseResult sendMsg(Long id, Long loanApplicationId);

    //自动还款通知
    void noticeUser();
}
