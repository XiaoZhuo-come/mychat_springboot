package com.xz.controller;

import com.alibaba.fastjson.JSONObject;
import com.xz.server.impl.MessageInfoServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
public class MsgHandler {

    @Autowired
    private MessageInfoServiceImpl mMessageInfoServer;
    //根据uid获取好友信息
    @GetMapping("/getFriendMsgByUid")
    private String getFriendMegByUid(String uid1,String uid2){
        return JSONObject.toJSONString(mMessageInfoServer.getFriendMessage(uid1, uid2));
    }
    //根据uid获取好友信息并且分页
    @GetMapping("/getFriendMsgPageByUid")
    private String getFriendMegByPageUid(String uid1,String uid2,int pageIndex){
        return JSONObject.toJSONString(mMessageInfoServer.getFriendMessageByPage(uid1, uid2,pageIndex));
    }

    //根据gid获取好友信息
    @GetMapping("/getGroupMsgByGid")
    private String getFriendMegByGid(String gid){
        return JSONObject.toJSONString(mMessageInfoServer.getGroupMessage(gid));
    }
    //根据gid获取好友信息并且分页
    @GetMapping("/getGroupMsgPageByGid")
    private String getGroupMegByPageUid(String gid,int pageIndex){
        return JSONObject.toJSONString(mMessageInfoServer.getGroupMessageByPage(gid,pageIndex));
    }

    //根据key删除数据
    @GetMapping("/delByKey")
    private Boolean delByKey(String key){
        if(StringUtils.isBlank(key)){
            return false;
        }
        return mMessageInfoServer.delValueByKey(key);
    }
}
