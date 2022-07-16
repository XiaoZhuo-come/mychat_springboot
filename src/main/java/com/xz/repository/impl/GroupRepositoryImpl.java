package com.xz.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xz.entity.GroupInfo;
import com.xz.entity.None;
import com.xz.mapper.GroupMapper;
import com.xz.repository.GroupRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupRepositoryImpl implements GroupRepository {
    @Autowired
    private GroupMapper mGroupMapper;

    @Override
    public List<GroupInfo> findGroupByUid(String uid) {
        return mGroupMapper.findGroupByUid(uid);
    }

    @Override
    public List<None> findUidByGid(String gid) {
        return mGroupMapper.findUidByGid(gid);
    }

    @Override
    public GroupInfo findGroupByGid(String gid) {
        QueryWrapper<GroupInfo> groupInfoQueryWrapper = new QueryWrapper<>();
        groupInfoQueryWrapper.eq("gid",gid);
        List<GroupInfo> groupInfoList = mGroupMapper.selectList(groupInfoQueryWrapper);
        if(groupInfoList!=null&&groupInfoList.size()>0){
            return groupInfoList.get(0);
        }
        return null;
    }

    @Override
    public int addGroup(GroupInfo groupInfo) {
        return mGroupMapper.insert(groupInfo);
    }

    @Override
    public List<None> isJoinGroup(String gid, String uid) {
        return mGroupMapper.isJoinGroup(gid, uid);
    }

    @Override
    public int joinGroup(String gid, String uid) {
        return mGroupMapper.joinGroup(gid, uid);
    }

    @Override
    public List<None> isExistGroup(String gid) {
        return mGroupMapper.isExistGroup(gid);
    }

    @Override
    public int deleteGroupInfoByGidUid(String gid,String uid) {
        return mGroupMapper.deleteGroupInfoByGidUid(gid,uid);
    }


    @Override
    public int deleteAllJoinGroupByGid(String gid) {
        return mGroupMapper.deleteAllJoinGroupByGid(gid);
    }

    @Override
    public int deleteGroupByGidUid(String gid, String uid) {
        return mGroupMapper.deleteGroupByGidUid(gid,uid);
    }

    @Override
    public List<GroupInfo> findGroupInfoByIdUid(String id, String uid) {
        QueryWrapper<GroupInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gid",id);
        queryWrapper.eq("uid",uid);
        return mGroupMapper.selectList(queryWrapper);
    }

    @Override
    public int updateGroup(GroupInfo groupInfo) {
        GroupInfo myGroupInfo = findGroupByGid(groupInfo.getGid());
        String avatar = groupInfo.getAvatar();
        String name = groupInfo.getName();
        if(StringUtils.isNotBlank(name)){
            myGroupInfo.setName(name);
        }
        if(StringUtils.isNotBlank(avatar)){
            myGroupInfo.setAvatar(avatar);
        }
        int line = mGroupMapper.updateById(myGroupInfo);
        return line;
    }
}
