package com.xz.server.impl;

import com.xz.entity.Apply;
import com.xz.entity.ApplyOVUser;
import com.xz.repository.impl.ApplyRepositoryImpl;
import com.xz.server.ApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplyServiceImpl implements ApplyService {
    @Autowired
    private ApplyRepositoryImpl mApplyRepository;
    @Override
    public int sendApply(Apply apply) {
        return mApplyRepository.sendApply(apply);
    }

    @Override
    public int agreeApply(String id,String uid) {
        return mApplyRepository.agreeApply(id,uid);
    }

    @Override
    public List<ApplyOVUser> getMyApply(String uid) {
        return mApplyRepository.getMyApply(uid);
    }


}
