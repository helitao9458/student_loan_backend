package com.example.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.pojo.RepaymentSchedule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RepaymentScheduleDao extends BaseMapper<RepaymentSchedule> {
    List<Integer> selectAllType(Long loanApplicationId);
}
