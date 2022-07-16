package com.xz.server.impl;

import com.xz.entity.User;
import com.xz.repository.impl.UserRepositoryImpl;
import com.xz.server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepositoryImpl mUserRepository;

    @Override
    public List<User> findFriendByUid(String uid) {
        return mUserRepository.findFriendByUid(uid);
    }

    @Override
    public List<User> selectByMap(Map<String, Object> map) {
        return mUserRepository.selectByMap(map);
    }

    @Override
    public User findByUid(String uid) {
        return mUserRepository.findByUid(uid);
    }

    @Override
    public int addUser(User user) {
        return mUserRepository.addUser(user);
    }

    @Override
    public Long findByUidCount(String uid) {
        return mUserRepository.findByUidCount(uid);
    }

    @Override
    public int updateUser(User user) {
        return mUserRepository.updateUser(user);
    }

    @Override
    public Long findByUidAndPwdCount(String uid, String pwd) {
        return mUserRepository.findByUidAndPwdCount(uid, pwd);
    }

    @Override
    public int addFriend(String uid, String toUid) {
        return mUserRepository.addFriend(uid,toUid);
    }

    @Override
    public int findFriendExist(String uid, String toUid) {
        return mUserRepository.findFriendExist(uid,toUid);
    }

    @Override
    public int deleteFriend(String uid, String toUid) {
        return mUserRepository.deleteFriend(uid, toUid);
    }

}
