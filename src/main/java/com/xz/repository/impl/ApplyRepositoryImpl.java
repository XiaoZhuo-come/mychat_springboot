package com.xz.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xz.entity.Apply;
import com.xz.entity.ApplyOVUser;
import com.xz.mapper.ApplyMapper;
import com.xz.repository.ApplyRepository;
import com.xz.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApplyRepositoryImpl implements ApplyRepository {
    @Autowired
    private ApplyMapper mApplyMapper;
    @Override
    public int sendApply(Apply apply) {
        QueryWrapper<Apply> applyQueryWrapper = new QueryWrapper<>();
        applyQueryWrapper.eq("uid",apply.getUid());
        applyQueryWrapper.eq("to_uid",apply.getToUid());
        applyQueryWrapper.eq("state",'0');
        List<Apply> applyList = mApplyMapper.selectList(applyQueryWrapper);
        if(applyList!=null&&applyList.size()==1){
            Apply tmp = applyList.get(0);
            tmp.setCreateTime(DateUtil.getNowTime());
            return mApplyMapper.updateById(tmp);
        }
        return mApplyMapper.insert(apply);
    }

    @Override
    public int agreeApply(String id,String uid) {
        QueryWrapper<Apply> applyQueryWrapper = new QueryWrapper<>();
        applyQueryWrapper.eq("id",id);
        applyQueryWrapper.eq("to_uid",uid);
        Apply apply = mApplyMapper.selectList(applyQueryWrapper).get(0);
        apply.setState(true);
        return mApplyMapper.updateById(apply);
    }

    @Override
    public List<ApplyOVUser> getMyApply(String uid) {
        return mApplyMapper.getMyApply(uid);
    }
}
