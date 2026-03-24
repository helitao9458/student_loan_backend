package com.example.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.pojo.LoanDisbursement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanDisbursementDao extends BaseMapper<LoanDisbursement> {
}
