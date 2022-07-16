package com.xz.server.impl;

import com.xz.entity.DynamicInfo;
import com.xz.entity.None;
import com.xz.repository.impl.DynamicRepositoryImpl;
import com.xz.server.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DynamicServiceImpl implements DynamicService {
    @Autowired
    private DynamicRepositoryImpl mDynamicRepository;
    @Override
    public List<DynamicInfo> getMyDynamic(String uid) {
        return mDynamicRepository.getMyDynamic(uid);
    }

    @Override
    public List<DynamicInfo> getMyselfDynamic(String uid) {
        return mDynamicRepository.getMyselfDynamic(uid);
    }

    @Override
    public int addComment(String uid, String did, String content, String time) {
        return mDynamicRepository.addComment(uid, did, content, time);
    }

    @Override
    public int addDynamic(String uid, String create_time, String content_text, String content_img, String like_num) {
        return mDynamicRepository.addDynamic(uid, create_time, content_text, content_img, like_num);
    }

    @Override
    public int likeDynamic(String uid, String did) {
        return mDynamicRepository.likeDynamic(uid, did);
    }

    @Override
    public int cancelLikeDynamic(String uid, String did) {
        return mDynamicRepository.cancelLikeDynamic(uid, did);
    }

    @Override
    public List<None> isLikeDynamic(String uid, String did) {
        return mDynamicRepository.isLikeDynamic(uid, did);
    }

    @Override
    public int deleteCommentByDid(String did) {
        return mDynamicRepository.deleteCommentByDid(did);
    }

    @Override
    public int deleteMyselfDynamic(String uid,String id) {
        return mDynamicRepository.deleteMyselfDynamic(uid,id);
    }

    @Override
    public int deleteDynamicLikeByDid(String did) {
        return mDynamicRepository.deleteDynamicLikeByDid(did);
    }

    @Override
    public List<None> selectDynamicByUidId(String uid, String id) {
        return mDynamicRepository.selectDynamicByUidId(uid, id);
    }

    @Override
    public List<None> selectDynamicById(String id) {
        return mDynamicRepository.selectDynamicById(id);
    }

}
