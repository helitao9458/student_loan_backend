package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.dao.LoanApplicationDao;
import com.example.backend.dao.RepaymentRecordDao;
import com.example.backend.dao.RepaymentScheduleDao;
import com.example.backend.dao.UserDao;
import com.example.backend.pojo.*;
import com.example.backend.service.RepaymentScheduleService;
import com.example.backend.tools.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RepaymentScheduleImpl implements RepaymentScheduleService {
    @Autowired
    private RepaymentScheduleDao repaymentScheduleDao;
    @Autowired
    private LoanApplicationDao loanApplicationDao;
    @Autowired
    private RepaymentRecordDao repaymentRecordDao;
    @Autowired
    private UserDao userDao;


    @Override
    public ResponseResult getSchedule() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();
        // 查询当前用户的审核通过并发放了贷款的贷款申请编号
        QueryWrapper<LoanApplication> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("user_id",userId);
        List<LoanApplication> loanApplications = loanApplicationDao.selectList(queryWrapper1);
        List<Long> loanApplicationIds = loanApplications.stream()
                .map(LoanApplication::getId)
                .collect(Collectors.toList());
        // 如果没有找到贷款申请，返回空结果
        if (loanApplicationIds.isEmpty()) {
            return ResponseResult.success(new ArrayList<>()); // 或者返回一个适当的消息
        }
        // 查询对应的还款计划
        QueryWrapper<RepaymentSchedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("loan_application_id", loanApplicationIds);
        List<RepaymentSchedule> schedules = repaymentScheduleDao.selectList(queryWrapper);
        // 返回结果
        return ResponseResult.success(schedules);
    }

    @Override
    public ResponseResult updateStatus(Long id, Integer amount, String type,Long loanApplicationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();
        // 修改状态计划表状态为1
        QueryWrapper<RepaymentSchedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id); // 根据 ID 查询
        // 创建更新对象并设置状态
        RepaymentSchedule repaymentSchedule = new RepaymentSchedule();
        repaymentSchedule.setStatus(1); // 设置状态为 1
        // 执行更新
        int updateCount = repaymentScheduleDao.update(repaymentSchedule, queryWrapper);
        if (updateCount == 0) {
            return ResponseResult.error("更新还款计划状态失败，找不到相应记录");
        }
        //同时添加还款记录
        RepaymentRecord repaymentRecord = new RepaymentRecord();
        repaymentRecord.setRepaymentScheduleId(id);
        repaymentRecord.setLoanApplicationId(loanApplicationId);
        repaymentRecord.setPaymentDate(LocalDateTime.now());
        repaymentRecord.setUserId(userId);
        repaymentRecord.setPaymentMethod(type);
        repaymentRecord.setPaymentAmount(amount);
        repaymentRecordDao.insert(repaymentRecord);
        //再判断是否换的贷款是1期还是12期
        if(loanApplicationDao.selectById(loanApplicationId).getType()==0){//1期还清
            loanApplicationDao.updateStatus(loanApplicationId,4);
        }else {
            List<Integer> list = repaymentScheduleDao.selectAllType(loanApplicationId);
            // 使用Stream API判断列表中是否全是1
            boolean allOnes = list.stream().allMatch(i -> i == 1);
            if (allOnes) {
                loanApplicationDao.updateStatus(loanApplicationId,4);
            }
        }
        return ResponseResult.success("还款成功");
    }

    @Override
    public IPage<RepaymentSchedule> getAll(Page<RepaymentSchedule> repaymentSchedulePage, Integer status) {
        QueryWrapper<RepaymentSchedule> queryWrapper = new QueryWrapper<>();
        if(status!=-1){
            queryWrapper.eq("status",status);
        }
        queryWrapper.orderByAsc("create_time");
        // 执行分页查询
        IPage<RepaymentSchedule> scheduleIPage = repaymentScheduleDao.selectPage(repaymentSchedulePage, queryWrapper);
        return scheduleIPage;
    }

    @Override
    public ResponseResult sendMsg(Long id, Long loanApplicationId) {
        // 根据 ID 查询 RepaymentSchedule 对象
        RepaymentSchedule repaymentSchedule = repaymentScheduleDao.selectById(id);

        if (repaymentSchedule != null) {
            // 设置提醒时间为当前时间
            repaymentSchedule.setReminderTime(LocalDateTime.now());
            // 更新 repaymentSchedule 对象到数据库
            repaymentScheduleDao.updateById(repaymentSchedule);
            //根据贷款审核编号查询用户编号
            LoanApplication loanApplication = loanApplicationDao.selectById(loanApplicationId);
            Long userId = loanApplication.getUserId();
            //根据用户id获取对应的邮箱信息
            User user = userDao.selectById(userId);
            String email = user.getEmail();
            String msg = "你有一笔贷款还未还款，请尽快登录国家助学贷款系统还款，以免影响个人信用。"+"还款编号为"+id;
            EmailUtil.sendCustomEmail(email,"还款通知",msg);
            // 可以根据需求返回一个成功的响应结果
            return new ResponseResult(200, "提醒成功");
        } else {
            // 如果找不到对应的 repaymentSchedule 对象
            return new ResponseResult(404, "提醒失败");
        }
    }

    //秒，分，时，日，月，星期（？代表不关心）
    @Scheduled(cron = "0 16 11 * * ?")
    @Override
    public void noticeUser() {
        // 查找状态为0（未还款）和2（已逾期）的记录
        Integer fine = 50;
        QueryWrapper<RepaymentSchedule> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("status", 0, 2); // 状态为0（未还款）或2（已逾期）

        List<RepaymentSchedule> list = repaymentScheduleDao.selectList(queryWrapper);
        if (list.isEmpty()) {
            log.info("暂时没有未还款或逾期记录");
        } else {
            for (RepaymentSchedule repaymentSchedule : list) {
                // 获取还款截至日期
                LocalDateTime dueDate = repaymentSchedule.getDueDate();
                LocalDateTime currentDateTime = LocalDateTime.now();

                // 判断当前日期是否等于或晚于还款日期
                if (!currentDateTime.isBefore(dueDate)) {
                    // 计算逾期的月数（仅当 overdueDays > 0 时，说明当前日期晚于还款日期）
                    long monthsBetween = ChronoUnit.MONTHS.between(dueDate, currentDateTime);

                    // 如果逾期，更新状态并计算罚金
                    if (monthsBetween > 0) {
                        Integer totalFine = fine * (int) monthsBetween;
                        repaymentSchedule.setFine(totalFine);
                        repaymentSchedule.setStatus(2); // 更新状态为2，表示逾期
                    } else if (currentDateTime.toLocalDate().isEqual(dueDate.toLocalDate())) {
                        // 当前日期与还款日期相等，设置罚金为50
                        repaymentSchedule.setFine(0);
                    }else{//还款时间大于当前时间
                        // 当前月份即还款月份，设置罚金为50
                        repaymentSchedule.setFine(fine);
                        repaymentSchedule.setStatus(2); // 更新状态为2，表示逾期
                    }

                    repaymentSchedule.setReminderTime(currentDateTime); // 更新提醒时间
                    repaymentScheduleDao.updateById(repaymentSchedule); // 更新 repaymentSchedule 对象到数据库

                    // 根据贷款审核编号查询用户编号
                    LoanApplication loanApplication = loanApplicationDao.selectById(repaymentSchedule.getLoanApplicationId());
                    Long userId = loanApplication.getUserId();

                    // 根据用户ID获取对应的邮箱信息
                    User user = userDao.selectById(userId);
                    String email = user.getEmail();

                    // 通知用户还款
                    String msg = "您有一笔贷款未还款，请尽快登录国家助学贷款系统还款，以免影响个人信用。" + "还款编号为 " + repaymentSchedule.getId();
                    EmailUtil.sendCustomEmail(email, "自动还款通知", msg);
                } else {
                    // 如果当前日期早于还款日期，则更新状态为未逾期（或保持未还款状态）
                    repaymentSchedule.setStatus(0); // 状态为0，表示未逾期的未还款
                    repaymentScheduleDao.updateById(repaymentSchedule);
                }
            }
        }
    }
}
