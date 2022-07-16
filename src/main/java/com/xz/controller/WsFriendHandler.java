package com.xz.controller;

import com.xz.entity.MessageInfo;
import com.xz.entity.RespModel;
import com.xz.server.impl.MessageInfoServiceImpl;
import com.xz.util.BeanUtil;
import com.xz.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component
@DependsOn("springContext")
@ServerEndpoint("/friend/{uid}/{name}/{token}")
public class WsFriendHandler {

    private MessageInfoServiceImpl mMessageInfoServer = BeanUtil.getBean(MessageInfoServiceImpl.class);

    static Log log = LogFactory.getLog(WsFriendHandler.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WsFriendHandler> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String mUserId = "";
    private String mUserName = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("uid") String uid,@PathParam("name") String name,@PathParam("token") String token) throws IOException {
        String tokenIsVerify = JwtUtil.isVerify(token);
        if(com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(token)||!tokenIsVerify.equals("OK")){
            session.close();
            return;
        }
        this.session = session;
        this.mUserId = uid;
        this.mUserName = name;
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
        log.info("友聊用户连接=>:uid:" + uid +" name:"+name+ ",当前在线人数为:" + getOnlineCount());

        try {
            sendMessage("{\"message\":\"友聊连接成功\"}");
        } catch (IOException e) {
            log.error("用户:" + uid + ",网络异常!!!!!!");
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + mUserId + ",报文:" + message);

        if (StringUtils.isNotBlank(message)) {
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                //保存信息到redis
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setFromUid(jsonObject.getString("fromUid"));
                messageInfo.setFromName(jsonObject.getString("fromName"));
                messageInfo.setToUid(jsonObject.getString("toUid"));
                messageInfo.setMessage(jsonObject.getString("message"));
                mMessageInfoServer.saveFriendMessage(messageInfo);
                //给目标uid
                String toUid = jsonObject.getString("toUid");

                if (StringUtils.isNotBlank(toUid) && webSocketMap.containsKey(toUid)) {
                    webSocketMap.get(toUid).sendMessage(jsonObject.toJSONString());
                    //webSocketMap.get(toUid).session.getBasicRemote().sendText(jsonObject.toJSONString());
                } else {
                    log.error("请求的uid:" + toUid + "不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.mUserId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(mUserId)) {
            webSocketMap.remove(mUserId);
            //从set中删除
            if(webSocketMap.size()>0){
                subOnlineCount();
            }
        }
        log.info("用户退出:" + mUserId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:" + userId + "，报文:" + message);
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户" + userId + ",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WsFriendHandler.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WsFriendHandler.onlineCount--;
    }
}
