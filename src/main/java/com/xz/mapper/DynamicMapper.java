package com.xz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xz.entity.DynamicInfo;
import com.xz.entity.None;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DynamicMapper extends BaseMapper<DynamicInfo> {
    public List<DynamicInfo> getMyDynamic(String uid);

    public List<DynamicInfo> getMyselfDynamic(String uid);
    @Insert("insert t_comment(did, uid, content_text, create_time) values ('${did}','${uid}','${content}','${time}')")
    public int addComment(String uid,String did,String content,String time);

    @Insert("insert t_dynamic(uid, create_time, content_text, content_img, like_num) VALUES ('${uid}','${create_time}','${content_text}','${content_img}','${like_num}')")
    public int addDynamic(String uid,String create_time,String content_text,String content_img,String like_num);

    @Select("select id from t_dynamic_like where uid='${uid}' and did='${did}'")
    public List<None> isLikeDynamic(String uid,String did);
    @Insert("insert t_dynamic_like(uid, did) VALUES ('${uid}','${did}')")
    public int likeDynamic(String uid,String did);
    @Delete("delete from t_dynamic_like where uid='${uid}' and did='${did}'")
    public int cancelLikeDynamic(String uid,String did);

    @Delete("delete from t_comment where did ='${did}';")
    public int deleteCommentByDid(String did);

    @Delete("delete from t_dynamic_like where did ='${did}';")
    public int deleteDynamicLikeByDid(String did);

    @Delete("delete from t_dynamic where id ='${id}' and uid='${uid}';")
    public int deleteMyselfDynamic(String uid,String id);

    @Select("select id from t_dynamic where uid='${uid}' and id ='${id}'")
    public List<None> selectDynamicByUidId(String uid,String id);

    @Select("select id from t_dynamic where id ='${id}'")
    public List<None> selectDynamicById(String id);

}
