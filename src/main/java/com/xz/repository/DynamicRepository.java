package com.xz.repository;

import com.xz.entity.DynamicInfo;
import com.xz.entity.None;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

public interface DynamicRepository {
    public List<DynamicInfo> getMyDynamic(String uid);

    public List<DynamicInfo> getMyselfDynamic(String uid);

    public int addComment(String uid,String did,String content,String time);

    public int addDynamic(String uid,String create_time,String content_text,String content_img,String like_num);


    public int likeDynamic(String uid,String did);

    public int cancelLikeDynamic(String uid,String did);

    public List<None> isLikeDynamic(String uid, String did);

    public int deleteCommentByDid(String did);

    public int deleteDynamicLikeByDid(String did);

    public int deleteMyselfDynamic(String uid,String id);

    public List<None> selectDynamicByUidId(String uid,String id);
    public List<None> selectDynamicById(String id);
}
