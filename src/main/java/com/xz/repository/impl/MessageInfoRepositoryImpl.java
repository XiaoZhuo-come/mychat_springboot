package com.xz.repository.impl;

import com.xz.entity.MessageInfo;
import com.xz.repository.MessageInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
public class MessageInfoRepositoryImpl implements MessageInfoRepository {

    //限制数据大小
    private static final Long LIMIT_SIZE = 10000L;
    //每页数据条数
    private static final int PAGE_SIZE=10;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void saveFriendMessage(MessageInfo messageInfo) {

        String key = getUidKey(messageInfo.getFromUid(),messageInfo.getToUid());
        ListOperations<String,MessageInfo> listOperations = redisTemplate.opsForList();
        listOperations.leftPush(key,messageInfo);

        limitSize(listOperations,key);
    }

    @Override
    public List<MessageInfo> getFriendMessage(String uid1, String uid2) {
        String key = getUidKey(uid1,uid2);
        List<MessageInfo> list = redisTemplate.opsForList().range(key,0,-1);
        return list;
    }


    @Override
    public List<MessageInfo> getFriendMessageByPage(String uid1, String uid2, int pageIndex) {
        String key = getUidKey(uid1,uid2);
        int start = (pageIndex-1)*PAGE_SIZE;
        int end = (pageIndex-1)*PAGE_SIZE+PAGE_SIZE-1;
        List<MessageInfo> list = redisTemplate.opsForList().range(key,start,end);
        return list;
    }



    @Override
    public void saveGroupMessage(MessageInfo messageInfo) {
        ListOperations<String,MessageInfo> listOperations = redisTemplate.opsForList();
        String key = "gid_"+messageInfo.getToGid();
        listOperations.leftPush(key,messageInfo);
        limitSize(listOperations,key);
    }

    @Override
    public List<MessageInfo> getGroupMessage(String gid) {
        String key = "gid_"+gid;
        List<MessageInfo> list = redisTemplate.opsForList().range(key,0,-1);
        return list;
    }

    @Override
    public List<MessageInfo> getGroupMessageByPage(String gid, int pageIndex) {
        String key = "gid_"+gid;
        int start = (pageIndex-1)*PAGE_SIZE;
        int end = (pageIndex-1)*PAGE_SIZE+PAGE_SIZE-1;
        List<MessageInfo> list = redisTemplate.opsForList().range(key,start,end);
        return list;
    }

    @Override
    public Boolean delValueByKey(String key) {
        return redisTemplate.delete(key);
    }

    //获取key
    private String getUidKey(String uid1,String uid2){
        String key = "uid_"+uid1+"_"+uid2;
        if(redisTemplate.hasKey(key)){
            return key;
        }else{
            key = "uid_"+uid2+"_"+uid1;
        }
        return key;
    }
    //限制数据大小
    private void limitSize(ListOperations listOperations,String key){
        Long size = listOperations.size(key);
        if(size != null && size > LIMIT_SIZE){
            listOperations.trim(key,0,LIMIT_SIZE);
        }
    }
}
