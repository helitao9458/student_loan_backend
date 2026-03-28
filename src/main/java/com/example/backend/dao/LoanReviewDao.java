package com.example.backend.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.pojo.LoanReview;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanReviewDao extends BaseMapper<LoanReview> {
}
