package com.xz.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xz.entity.GroupInfo;
import com.xz.entity.MessageInfo;
import com.xz.entity.None;
import com.xz.entity.RespModel;
import com.xz.server.impl.GroupServiceImpl;
import com.xz.server.impl.MessageInfoServiceImpl;
import com.xz.util.BeanUtil;
import com.xz.util.JwtUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Component
@DependsOn("springContext")
@ServerEndpoint("/group/{uid}/{name}/{token}")

public class WsGroupHandler {

    private MessageInfoServiceImpl mMessageInfoServer = BeanUtil.getBean(MessageInfoServiceImpl.class);

    private static GroupServiceImpl mGroupServer = BeanUtil.getBean(GroupServiceImpl.class);;

    static Log log = LogFactory.getLog(WsGroupHandler.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WsGroupHandler> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String mUid = "";
    private static List<String> mUids;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid,@PathParam("name") String name,@PathParam("token") String token) throws IOException {
        String tokenIsVerify = JwtUtil.isVerify(token);
        if(StringUtils.isBlank(token)||!tokenIsVerify.equals("OK")){
            session.close();
            return;
        }
        this.session = session;
        this.mUid = uid;
        if (webSocketMap.containsKey(uid)) {
            webSocketMap.remove(uid);
            webSocketMap.put(uid, this);
            //加入set中
        } else {
            webSocketMap.put(uid, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        log.info("群聊用户连接=>:uid:" + uid +" name:"+name+ ",当前在线人数为:" + getOnlineCount());

        try {
            sendMessage("{\"message\":\"群聊连接成功\"}");
        } catch (IOException e) {
            log.error("用户:" + uid + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(mUid)) {
            webSocketMap.remove(mUid);
            //从set中删除
            subOnlineCount();
        }
        log.info("用户退出:" + mUid + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("用户消息:" + mUid + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis

        if (StringUtils.isNotBlank(message)) {
            JSONObject jsonObject = JSON.parseObject(message);
            GroupInfo groupInfo = mGroupServer.findGroupByGid(jsonObject.getString("toGid"));
            if(groupInfo==null){
                RespModel respModel = new RespModel();
                respModel.setCode(400);
                respModel.setMessage("该群聊已经不存在，消息发送失败！");
                sendMessage(JSONObject.toJSONString(respModel));
            }else {
                //保存信息到redis
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setFromUid(jsonObject.getString("fromUid"));
                messageInfo.setFromName(jsonObject.getString("fromName"));
                messageInfo.setFromGName(jsonObject.getString("fromGName"));
                messageInfo.setToGid(jsonObject.getString("toGid"));
                messageInfo.setMessage(jsonObject.getString("message"));
                messageInfo.setFromAvatar(jsonObject.getString("fromAvatar"));
                mMessageInfoServer.saveGroupMessage(messageInfo);
                //根据gid获取所有uid
                String toGid = jsonObject.getString("toGid");
                List<None> groupInfos = mGroupServer.findUidByGid(toGid);
                mUids = groupInfos.stream().map(None::getUid).collect(Collectors.toList());
                //this.mUid = jsonObject.getString("fromUid");
                //传送给对应toUserId用户的websocket
                if (StringUtils.isNotBlank(toGid)) {
                    webSocketMap.get(mUid).sendMessageAll(jsonObject.toJSONString());
                    //webSocketMap.get(toUserId).session.getBasicRemote().sendText(jsonObject.toJSONString());
                } else {
                    log.error("请求的Gid:" + toGid + "不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                }
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.mUid + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessageAll(String message) throws IOException {
        List<String> onlineUids = new ArrayList<>();
        for (String item:webSocketMap.keySet()){
            onlineUids.add(item);
        }
        System.out.println("uid："+mUid+"发送");
        for (String uid : mUids) {
            if(onlineUids.contains(uid)&&!mUid.equals(uid)){
                webSocketMap.get(uid).session.getAsyncRemote().sendText(message);
            }else {
                log.info("uid："+uid+"不在服务器上");
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        this.session.getAsyncRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WsGroupHandler.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WsGroupHandler.onlineCount--;
    }
}
