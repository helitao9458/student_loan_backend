package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.common.ResponseResult;
import com.example.backend.dao.PolicyDao;
import com.example.backend.pojo.Policy;
import com.example.backend.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class PolicyServiceImpl implements PolicyService {
    @Autowired
    PolicyDao policyDao;

    @Override
    public IPage<Policy> findAllPolicies(Page<Policy> policyPage, Integer policyType, String title) {
        // 构建查询条件
        QueryWrapper<Policy> queryWrapper = new QueryWrapper<>();
        // 添加政策类型的查询条件（如果有提供）
        if (policyType != null && policyType != 0) { // 如果没有类别时默认查询所有
            queryWrapper.eq("type", policyType);
        }
        // 添加标题模糊查询的条件（如果有提供）
        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("title", title);
        }
        // 添加状态的条件，假设 state=0 表示有效政策
        queryWrapper.eq("state", 0);
        queryWrapper.orderByDesc("create_time");
        // 执行分页查询
        IPage<Policy> policies = policyDao.selectPage(policyPage, queryWrapper);
        return policies;
    }

    @Override
    public int addPolicy(Policy policy) {
        return policyDao.insertPolicy(policy);
    }

    @Override
    public ResponseResult updateState(Long id, Integer state) {
        // 更新状态并获取结果
        int rowsAffected = policyDao.updateStateById(id, state);
        if (rowsAffected > 0) {
            return ResponseResult.success("状态更新成功");
        } else {
            return ResponseResult.error("状态更新失败");
        }
    }

    @Override
    public ResponseResult deleteById(Long id) {
        // 删除记录并获取结果
        int rowsAffected = policyDao.deleteById(id);
        if (rowsAffected > 0) {
            return ResponseResult.success("政策删除成功");
        } else {
            return ResponseResult.error("政策删除失败，未找到该ID");
        }
    }

    @Override
    public IPage<Policy> findAllPolicie(Page<Policy> policyPage, Integer policyType, String title) {
        // 构建查询条件
        QueryWrapper<Policy> queryWrapper = new QueryWrapper<>();
        // 添加政策类型的查询条件（如果有提供）
        if (policyType != null && policyType != 0) { // 如果没有类别时默认查询所有
            queryWrapper.eq("type", policyType);
        }
        // 添加标题模糊查询的条件（如果有提供）
        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("title", title);
        }
        // 添加状态的条件，假设 state=0 表示有效政策
        queryWrapper.orderByDesc("create_time");
        // 执行分页查询
        IPage<Policy> policies = policyDao.selectPage(policyPage, queryWrapper);
        return policies;
    }

    @Override
    public Policy selectById(Long id) {
        return policyDao.selectById(id);
    }

    @Override
    public ResponseResult updatePolicy(Policy oldPolicy) {
        if(policyDao.updateById(oldPolicy)>0){
            return ResponseResult.success("修改成功");
        }else{
                return ResponseResult.success("修改失败");
        }
    }
}
