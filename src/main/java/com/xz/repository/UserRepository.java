package com.xz.repository;

import com.xz.entity.None;
import com.xz.entity.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    public List<User> findFriendByUid(String uid);

    public List<User> selectByMap(Map<String,Object> map);

    public User findByUid(String uid);

    public int addUser(User user);

    public Long findByUidCount(String uid);

    public int updateUser(User user);

    public Long findByUidAndPwdCount(String uid,String pwd);

    public int addFriend(String uid,String toUid);

    public int findFriendExist(String uid, String toUid);

    public int deleteFriend(String uid,String toUid);
}
