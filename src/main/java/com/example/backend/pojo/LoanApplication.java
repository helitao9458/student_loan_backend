package com.example.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("loan_application")
public class LoanApplication {
    @TableId(type = IdType.AUTO)
    private Long id;  // 编号
    private Long userId;  // 用户编号
    private String applicant;  // 申请人
    private Integer loanAmount;  // 贷款金额
    private String bankCard;  // 银行卡号
    private String idCard; //用户身份证号
    private String content;  // 贷款详细原因
    private Integer type;  // 还款类型：0为1年后一次性还清，1为一年后分12期还清
    private String imgs;  // 贷款资料（多张图片的路径）
    private String school;  // 所属学校
    private Integer status;  // 状态：0为待审核，1为已通过，2为未通过，3为未到账，4为已到账
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;  // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;  // 修改时间
}
