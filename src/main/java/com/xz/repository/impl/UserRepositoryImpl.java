package com.xz.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.regexp.internal.RE;
import com.xz.entity.None;
import com.xz.entity.User;
import com.xz.mapper.UserMapper;
import com.xz.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private UserMapper mUserMapper;

    @Override
    public List<User> findFriendByUid(String uid) {
        return mUserMapper.findFriendByUid(uid);
    }

    @Override
    public List<User> selectByMap(Map<String, Object> map) {
        return mUserMapper.selectByMap(map);
    }

    @Override
    public User findByUid(String uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",uid);
        List<User> users = mUserMapper.selectList(queryWrapper);
        if(users!=null&&users.size()==0){
            return null;
        }else {
            return users.get(0);
        }
    }

    @Override
    public int addUser(User user) {
        return mUserMapper.insert(user);
    }

    @Override
    public Long findByUidCount(String uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",uid);
        return mUserMapper.selectCount(queryWrapper);
    }

    @Override
    public int updateUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",user.getUid());
        return mUserMapper.update(user,queryWrapper);
    }

    @Override
    public Long findByUidAndPwdCount(String uid, String pwd) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("uid",uid).
                eq("password",pwd);
        return mUserMapper.selectCount(queryWrapper);
    }

    @Override
    public int addFriend(String uid, String toUid) {
        return mUserMapper.addFriend(uid,toUid);
    }

    @Override
    public int findFriendExist(String uid, String toUid) {
        List<None> nones = mUserMapper.findFriendExist(uid,toUid);
        if(nones!=null&&nones.size()>0){
            return nones.size();
        }else{
            return 0;
        }
    }

    @Override
    public int deleteFriend(String uid, String toUid) {
        return mUserMapper.deleteFriend(uid, toUid);
    }


}
