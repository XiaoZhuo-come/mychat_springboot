package com.xz.server.impl;

import com.xz.entity.GroupInfo;
import com.xz.entity.None;
import com.xz.repository.impl.GroupRepositoryImpl;
import com.xz.server.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    private GroupRepositoryImpl mGroupRepository;

    @Override
    public List<GroupInfo> findGroupByUid(String uid) {
        return mGroupRepository.findGroupByUid(uid);
    }

    @Override
    public List<None> findUidByGid(String gid) {
        return mGroupRepository.findUidByGid(gid);
    }

    @Override
    public GroupInfo findGroupByGid(String gid) {
        return mGroupRepository.findGroupByGid(gid);
    }

    @Override
    public int addGroup(GroupInfo groupInfo) {
        return mGroupRepository.addGroup(groupInfo);
    }

    @Override
    public List<None> isJoinGroup(String gid, String uid) {
        return mGroupRepository.isJoinGroup(gid,uid);
    }

    @Override
    public int joinGroup(String gid, String uid) {
        return mGroupRepository.joinGroup(gid, uid);
    }

    @Override
    public List<None> isExistGroup(String gid) {
        return mGroupRepository.isExistGroup(gid);
    }


    @Override
    public int deleteGroupInfoByGidUid(String gid,String uid) {
        return mGroupRepository.deleteGroupInfoByGidUid(gid,uid);
    }

    @Override
    public int deleteAllJoinGroupByGid(String gid) {
        return mGroupRepository.deleteAllJoinGroupByGid(gid);
    }

    @Override
    public int deleteGroupByGidUid(String gid, String uid) {
        return mGroupRepository.deleteGroupByGidUid(gid, uid);
    }

    @Override
    public List<GroupInfo> findGroupInfoByIdUid(String id, String uid) {
        return mGroupRepository.findGroupInfoByIdUid(id, uid);
    }

    @Override
    public int updateGroup(GroupInfo groupInfo) {
        return mGroupRepository.updateGroup(groupInfo);
    }
}
