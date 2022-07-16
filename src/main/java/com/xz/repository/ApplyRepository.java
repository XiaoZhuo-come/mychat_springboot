package com.xz.repository;

import com.xz.entity.Apply;
import com.xz.entity.ApplyOVUser;

import java.util.List;

public interface ApplyRepository {
    public int sendApply(Apply apply);
    public int agreeApply(String id,String toUid);

    public List<ApplyOVUser> getMyApply(String uid);
}
