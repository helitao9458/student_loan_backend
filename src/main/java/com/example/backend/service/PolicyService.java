package com.example.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.pojo.Policy;

public interface PolicyService {
    IPage<Policy> findAllPolicies(Page<Policy> policyPage, Integer policyType, String title);
    int addPolicy(Policy policy);
    ResponseResult updateState(Long id, Integer state);
    ResponseResult deleteById(Long id);
    //管理员查看所有用户
    IPage<Policy> findAllPolicie(Page<Policy> policyPage, Integer policyType, String title);
    //根据id查询政策
    Policy selectById(Long id);

    ResponseResult updatePolicy(Policy oldPolicy);
}
