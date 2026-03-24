package com.example.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.pojo.LoanApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanApplicationDao extends BaseMapper<LoanApplication> {
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    List<Long> selectId(Long userid);
}
