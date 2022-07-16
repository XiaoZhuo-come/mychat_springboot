package com.xz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xz.entity.ApplyOVUser;
import com.xz.entity.None;
import com.xz.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select distinct tu.id,tu.uid,tu.username,tu.avatar from t_user tu,(select * from t_friend where t_friend.uid = '${uid}' or t_friend.to_uid='${uid}') tf where (tu.uid=tf.uid or tu.uid=tf.to_uid) and tu.uid!='${uid}'")
    public List<User> findFriendByUid(@Param("uid") String uid);

    //调换位置,以为toUid是发送申请的人所以在坐标，不管在那边都对结果都没影响
    @Insert("insert t_friend(uid, to_uid) values ('${toUid}','${uid}')")
    public int addFriend(String uid,String toUid);

    @Select("select id from t_friend where (uid='${uid}' and to_uid='${toUid}') or (uid='${toUid}' and to_uid='${uid}')")
    public List<None> findFriendExist(String uid,String toUid);

    @Delete("delete from t_friend where (uid='${uid}' and to_uid='${toUid}') or (uid='${toUid}' and to_uid='${uid}')")
    public int deleteFriend(String uid,String toUid);
}

