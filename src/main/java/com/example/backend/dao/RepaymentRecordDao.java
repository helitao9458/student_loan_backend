package com.example.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.pojo.RepaymentRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RepaymentRecordDao extends BaseMapper<RepaymentRecord> {
}
