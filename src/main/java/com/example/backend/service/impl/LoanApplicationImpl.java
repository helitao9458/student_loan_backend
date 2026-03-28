package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.dao.*;
import com.example.backend.pojo.*;
import com.example.backend.service.LoanApplicationService;
import com.example.backend.tools.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class LoanApplicationImpl implements LoanApplicationService {
    @Autowired
    private LoanApplicationDao loanApplicationDao;
    @Autowired
    private LoanReviewDao loanReviewDao;
    @Autowired
    private LoanDisbursementDao loanDisbursementDao;
    @Autowired
    private RepaymentScheduleDao repaymentScheduleDao;
    @Autowired
    private UserDao userDao;

    @Override
    public ResponseResult save(LoanApplication loanApplication) {
        int result = loanApplicationDao.insert(loanApplication);
        if (result > 0) {
            return ResponseResult.success("贷款申请提交成功");
        } else {
            return ResponseResult.error("贷款申请提交失败，请稍后重试");
        }
    }

    @Override
    public IPage<LoanApplication> findAllPolicies(Page<LoanApplication> loanPage, String applicant, Integer status, String school) {
        // 构建查询条件
        QueryWrapper<LoanApplication> queryWrapper = new QueryWrapper<>();
        // 添加政策类型的查询条件（如果有提供）
        if (status != -1) { // 如果没有类别时默认查询所有
            queryWrapper.eq("status", status);
        }
        if (!StringUtils.isEmpty(school)){
            queryWrapper.eq("school",school);
        }
        // 添加标题模糊查询的条件（如果有提供）
        if (!StringUtils.isEmpty(applicant)) {
            queryWrapper.like("applicant", applicant);
        }
        queryWrapper.orderByDesc("create_time");
        // 执行分页查询
        IPage<LoanApplication> loans = loanApplicationDao.selectPage(loanPage, queryWrapper);
        return loans;
    }

    @Override
    public ResponseResult getAllLoans() {
        // 使用 BaseMapper 提供的 selectList 方法查询所有贷款记录
        List<LoanApplication> loanList = loanApplicationDao.selectList(null);
        // 判断查询结果
        if (loanList == null || loanList.isEmpty()) {
            // 如果查询结果为空，返回一个失败的 ResponseResult
            return ResponseResult.error("没有找到贷款记录");
        }
        // 返回成功的 ResponseResult，并包含查询到的贷款记录
        return ResponseResult.success(loanList);
    }

    @Override
    public ResponseResult updateStatus(Long id, Integer status, String reviewComments) {
        //修改贷款申请表状态
        loanApplicationDao.updateStatus(id,status);
        //添加审核记录
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUser().getId();
        LoanReview loanReview = new LoanReview();
        loanReview.setLoanApplicationId(id);
        loanReview.setReviewerId(userid);
        loanReview.setReviewStatus(status);
        loanReview.setReviewComments(reviewComments);
        loanReview.setReviewTime(LocalDateTime.now());
        //将创建好的审核记录插入表中
        loanReviewDao.insert(loanReview);
        // 返回成功结果
        return ResponseResult.success("状态更新成功");
    }

    @Override
    public IPage<LoanApplication> getTrue(Page<LoanApplication> loanPage, String applicant, String school) {
        // 构建查询条件
        QueryWrapper<LoanApplication> queryWrapper = new QueryWrapper<>();
        // 添加政策类型的查询条件（如果有提供）
        if (!StringUtils.isEmpty(school)){
            queryWrapper.eq("school",school);
        }
        // 添加标题模糊查询的条件（如果有提供）
        if (!StringUtils.isEmpty(applicant)) {
            queryWrapper.like("applicant", applicant);
        }
        queryWrapper.eq("status", 1);
        queryWrapper.orderByDesc("create_time");
        // 执行分页查询
        IPage<LoanApplication> loans = loanApplicationDao.selectPage(loanPage, queryWrapper);
        return loans;
    }

    @Override
    public ResponseResult distribute(Long id) {
        //将贷款状态修改为已到账
        loanApplicationDao.updateStatus(id,3);
        //将放款记录到放款表
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        LoanApplication loanApplication = loanApplicationDao.selectById(id);
        LoanDisbursement loanDisbursement = new LoanDisbursement();
        loanDisbursement.setUserId(loginUser.getUser().getId());
        loanDisbursement.setBankName(loginUser.getUser().getBankName());
        loanDisbursement.setLoanApplicationId(loanApplication.getId());
        loanDisbursement.setDisbursementAmount(loanApplication.getLoanAmount());
        loanDisbursement.setDisbursementTime(LocalDateTime.now());
        loanDisbursementDao.insert(loanDisbursement);
        //根据贷款表的还款类型生成对应数量的还款条目
        if(loanApplication.getType() == 0){//一年后就还款
            RepaymentSchedule repaymentSchedule = new RepaymentSchedule();
            repaymentSchedule.setLoanApplicationId(loanApplication.getId());
            Random random = new Random();
            repaymentSchedule.setAmount(loanApplication.getLoanAmount()+random.nextInt(501) + 500);//模拟
            repaymentSchedule.setCreateTime(LocalDateTime.now());
            repaymentSchedule.setStatus(0);
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            // 调整日期到明年这个月的1号
            LocalDate dueDate = currentDate.withYear(currentDate.getYear() + 1)
                    .withDayOfMonth(1);
            // 将LocalDate转换为LocalDateTime（假设时间为当天的00:00:00）
            LocalDateTime dueDateTime = dueDate.atStartOfDay();
            // 设置还款日期
            repaymentSchedule.setDueDate(dueDateTime);
            repaymentScheduleDao.insert(repaymentSchedule);
        }else {//一年后，每个月1号还借款金额的十二分一加一个50到100的随机数，总共生成12条数据
            Random random = new Random();
            Integer monthlyRepaymentAmount = loanApplication.getLoanAmount() / 12; // 每月应还款金额

            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            // 从明年这个月开始
            LocalDate startDate = currentDate.withYear(currentDate.getYear() + 1).withDayOfMonth(1);

            for (int i = 0; i < 12; i++) {
                RepaymentSchedule repaymentSchedule = new RepaymentSchedule();
                repaymentSchedule.setLoanApplicationId(loanApplication.getId());
                // 计算还款金额
                Integer amount = monthlyRepaymentAmount + (random.nextInt(51) + 50); // 50到100的随机数
                repaymentSchedule.setAmount(amount);
                repaymentSchedule.setCreateTime(LocalDateTime.now());
                repaymentSchedule.setStatus(0);
                // 设置还款日期为每个月的1号
                LocalDate dueDate = startDate.plusMonths(i);
                LocalDateTime dueDateTime = dueDate.atStartOfDay();
                repaymentSchedule.setDueDate(dueDateTime);
                // 保存还款计划
                repaymentScheduleDao.insert(repaymentSchedule);
            }
        }
        User user = userDao.selectById(loanApplication.getUserId());
        EmailUtil.sendCustomEmail(user.getEmail(),"国家助学贷款系统","你的卡号为"+loanApplication.getBankCard()+"的银行卡收到助学贷款"+loanApplication.getLoanAmount()+"元");
        return ResponseResult.success("发放成功");
    }

    @Override
    public IPage<LoanApplication> getBySelf(Page<LoanApplication> loanPage, Integer status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUser().getId();
        QueryWrapper<LoanApplication> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(status)){
            queryWrapper.eq("status",status);
        }
        queryWrapper.eq("user_id",userid);
        IPage<LoanApplication> loans = loanApplicationDao.selectPage(loanPage, queryWrapper);
        return loans;
    }

}
