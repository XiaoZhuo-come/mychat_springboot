package com.xz.repository.impl;

import com.xz.entity.DynamicInfo;
import com.xz.entity.None;
import com.xz.mapper.DynamicMapper;
import com.xz.repository.DynamicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DynamicRepositoryImpl implements DynamicRepository {
    @Autowired
    private DynamicMapper mDynamicMapper;

    @Override
    public List<DynamicInfo> getMyDynamic(String uid) {
        return mDynamicMapper.getMyDynamic(uid);
    }

    @Override
    public List<DynamicInfo> getMyselfDynamic(String uid) {
        return mDynamicMapper.getMyselfDynamic(uid);
    }

    @Override
    public int addComment(String uid, String did, String content, String time) {
        return mDynamicMapper.addComment(uid,did,content,time);
    }

    @Override
    public int addDynamic(String uid, String create_time, String content_text, String content_img, String like_num) {
        return mDynamicMapper.addDynamic(uid, create_time, content_text, content_img, like_num);
    }

    @Override
    public int likeDynamic(String uid, String did) {
        return mDynamicMapper.likeDynamic(uid,did);
    }

    @Override
    public int cancelLikeDynamic(String uid, String did) {
        return mDynamicMapper.cancelLikeDynamic(uid,did);
    }

    @Override
    public List<None> isLikeDynamic(String uid, String did) {
        return mDynamicMapper.isLikeDynamic(uid, did);
    }

    @Override
    public int deleteCommentByDid(String did) {
        return mDynamicMapper.deleteCommentByDid(did);
    }

    @Override
    public int deleteMyselfDynamic(String uid,String id) {
        return mDynamicMapper.deleteMyselfDynamic(uid,id);
    }

    @Override
    public int deleteDynamicLikeByDid(String did) {
        return mDynamicMapper.deleteDynamicLikeByDid(did);
    }

    @Override
    public List<None> selectDynamicByUidId(String uid, String id) {
        return mDynamicMapper.selectDynamicByUidId(uid, id);
    }

    @Override
    public List<None> selectDynamicById(String id) {
        return mDynamicMapper.selectDynamicById(id);
    }
}
