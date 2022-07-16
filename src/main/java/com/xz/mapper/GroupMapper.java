package com.xz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xz.entity.GroupInfo;
import com.xz.entity.None;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupMapper extends BaseMapper<GroupInfo> {
    //根据uid获取加入的群聊信息
    @Select("select t_group_info.id,t_group_info.name,t_group_info.gid,t_group_info.uid,t_group_info.avatar from t_group_info,t_group where t_group_info.gid=t_group.gid and t_group.uid=${uid}")
    public List<GroupInfo> findGroupByUid(@Param("uid") String uid);
    //根据gid获取加入群聊的uid
    @Select("select t_group.uid from t_group_info,t_group where t_group_info.gid=t_group.gid and t_group.gid=${gid}")
    public List<None> findUidByGid(@Param("gid") String gid);

    @Select("select id from t_group where gid='${gid}' and uid='${uid}'")
    public List<None> isJoinGroup(String gid,String uid);

    @Select("select id from t_group where gid='${gid}'")
    public List<None> isExistGroup(String gid);

    @Insert("insert t_group(gid, uid) values('${gid}','${uid}')")
    public int joinGroup(String gid,String uid);

    @Delete("delete from t_group_info where gid='${gid}' and uid='${uid}'")
    public int deleteGroupInfoByGidUid(String gid,String uid);

    @Delete("delete from t_group where gid='${gid}'")
    public int deleteAllJoinGroupByGid(String gid);

    @Delete("delete from t_group where gid='${gid}' and uid='${uid}'")
    public int deleteGroupByGidUid(String gid, String uid);

//    @Select("select t_group_info.id from t_group_info where id='${id}' and gid='${uid}'")
//    public List<GroupInfo> findGroupInfoByIdUid(String id,String uid);
}
