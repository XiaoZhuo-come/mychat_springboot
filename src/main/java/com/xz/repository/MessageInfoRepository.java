package com.xz.repository;

import com.xz.entity.MessageInfo;

import java.util.List;

public interface MessageInfoRepository
{
    //保存好友聊天信息
    public void saveFriendMessage(MessageInfo messageInfo);
    //获取好友聊天信息
    public List<MessageInfo> getFriendMessage(String uid1,String uid2);
    //获取好友聊体信息
    public List<MessageInfo> getFriendMessageByPage(String uid1,String uid2,int pageIndex);

    //保存群聊天信息
    public void saveGroupMessage(MessageInfo messageInfo);
    //获取群聊天信息
    public List<MessageInfo> getGroupMessage(String gid);
    //获取群聊体信息
    public List<MessageInfo> getGroupMessageByPage(String gid,int pageIndex);

    //根据key删除值
    public Boolean delValueByKey(String key);

}
