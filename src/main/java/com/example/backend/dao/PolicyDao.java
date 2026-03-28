package com.example.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.pojo.Policy;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PolicyDao extends BaseMapper<Policy> {
    int insertPolicy(Policy policy);
    // 根据 id 更新 state 字段
    int updateStateById(@Param("id") Long id, @Param("state") Integer state);
    // 根据 id 删除 policy
    int deleteById(@Param("id") Long id);
}
