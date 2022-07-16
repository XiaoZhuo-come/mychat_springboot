package com.xz.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;

public class WsTestHandler implements WebSocketHandler {
    static Log log = LogFactory.getLog(WsFriendHandler.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WsTestHandler> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String mUserId = "";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String id = (String) session.getAttributes().get("id");
        this.mUserId =id;
        log.info("id="+id);
        if(!webSocketMap.containsKey("id")){
            webSocketMap.put(id,this);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("收到消息："+message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("发生错误："+this.mUserId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("关闭连接："+this.mUserId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
