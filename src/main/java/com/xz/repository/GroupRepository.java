package com.xz.repository;


import com.xz.entity.GroupInfo;
import com.xz.entity.None;

import java.util.List;

public interface GroupRepository {
    public List<GroupInfo> findGroupByUid(String uid);
    public List<None> findUidByGid(String gid);

    public GroupInfo findGroupByGid(String gid);

    public int addGroup(GroupInfo groupInfo);

    public List<None> isJoinGroup(String gid,String uid);

    public int joinGroup(String gid,String uid);
    public List<None> isExistGroup(String gid);

    public int deleteGroupInfoByGidUid(String gid,String uid);

    public int deleteAllJoinGroupByGid(String gid);

    public int deleteGroupByGidUid(String gid, String uid);

    public List<GroupInfo> findGroupInfoByIdUid(String id,String uid);

    public int updateGroup(GroupInfo groupInfo);

}
