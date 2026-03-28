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
@TableName("repayment_record")
public class RepaymentRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long repaymentScheduleId;
    private Long loanApplicationId;
    private Long userId;
    private Integer paymentAmount;//还款金额
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;//还款日期
    private String paymentMethod;
}
