package com.xz.server.impl;

import com.xz.entity.MessageInfo;
import com.xz.repository.impl.MessageInfoRepositoryImpl;
import com.xz.server.MessageInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageInfoServiceImpl implements MessageInfoService {
    @Autowired
    private MessageInfoRepositoryImpl mMessageInfoRepository;


    @Override
    public void saveFriendMessage(MessageInfo messageInfo) {
        mMessageInfoRepository.saveFriendMessage(messageInfo);
    }

    @Override
    public List<MessageInfo> getFriendMessage(String uid1, String uid2) {
        return mMessageInfoRepository.getFriendMessage(uid1,uid2);
    }


    @Override
    public List<MessageInfo> getFriendMessageByPage(String uid1, String uid2, int pageIndex) {
        return mMessageInfoRepository.getFriendMessageByPage(uid1, uid2, pageIndex);
    }

    @Override
    public void saveGroupMessage(MessageInfo messageInfo) {
        mMessageInfoRepository.saveGroupMessage(messageInfo);
    }

    @Override
    public List<MessageInfo> getGroupMessage(String gid) {
        return mMessageInfoRepository.getGroupMessage(gid);
    }

    @Override
    public List<MessageInfo> getGroupMessageByPage(String gid, int pageIndex) {
        return mMessageInfoRepository.getGroupMessageByPage(gid, pageIndex);
    }

    @Override
    public Boolean delValueByKey(String key) {
        return mMessageInfoRepository.delValueByKey(key);
    }
}
