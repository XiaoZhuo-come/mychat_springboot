package com.xz.server;

import com.xz.entity.Apply;
import com.xz.entity.ApplyOVUser;

import java.util.List;

public interface ApplyService {
    public int sendApply(Apply apply);
    public int agreeApply(String id,String uid);

    public List<ApplyOVUser> getMyApply(String uid);
}
