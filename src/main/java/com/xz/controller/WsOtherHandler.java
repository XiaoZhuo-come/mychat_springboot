package com.xz.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xz.entity.*;
import com.xz.server.MessageInfoService;
import com.xz.server.impl.ApplyServiceImpl;
import com.xz.server.impl.GroupServiceImpl;
import com.xz.server.impl.UserServiceImpl;
import com.xz.util.BeanUtil;
import com.xz.util.DateUtil;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@DependsOn("springContext")
@ServerEndpoint("/other/{uid}/{token}")
public class WsOtherHandler {

    private UserServiceImpl mUserServer = BeanUtil.getBean(UserServiceImpl.class);
    private ApplyServiceImpl mApplyService = BeanUtil.getBean(ApplyServiceImpl.class);

    private MessageInfoService mMessageInfoService = BeanUtil.getBean(MessageInfoService.class);
    private GroupServiceImpl mGroupService = BeanUtil.getBean(GroupServiceImpl.class);


    static Log log = LogFactory.getLog(WsGroupHandler.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WsOtherHandler> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String mUid = "";

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
        RespModel respModel = new RespModel();
        if (webSocketMap.containsKey(uid)) {
            respModel.setCode(400);
            respModel.setMessage("账号已在别处登陆");
            webSocketMap.get(uid).sendMessage(JSONObject.toJSONString(respModel));
            webSocketMap.get(uid).session.close();

            webSocketMap.remove(uid);
            webSocketMap.put(uid, this);
            //加入set中
        } else {
            webSocketMap.put(uid, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        log.info("连接成功");

        try {
            respModel.setCode(200);
            respModel.setMessage("登陆成功！");
            sendMessage(JSONObject.toJSONString(respModel));
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
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + mUid + ",报文:" + message);
        if(message.startsWith("\"")||message.endsWith("\"")){
            message = message.substring(1,message.length()-1).replace("\\","");
        }
        //可以群发消息
        //消息保存到数据库、redis
        if (StringUtils.isNotBlank(message)) {
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                String type = jsonObject.getString("type");
                switch (type){
                    case "好友申请":
                        sendApply(jsonObject.getString("toUid"),jsonObject.getString("token"));
                        break;
                    case "同意申请":
                        agreeApply(jsonObject.getString("id"),jsonObject.getString("uid"),jsonObject.getString("token"));
                        break;
                    case "删除好友":
                        deleteFriend(jsonObject.getString("toUid"),jsonObject.getString("token"));
                        break;
                    case "删除群聊":
                        deleteGroup(jsonObject.getString("gid"),jsonObject.getString("token"));
                        break;
                    case "退出群聊":
                        exitGroup(jsonObject.getString("gid"),jsonObject.getString("token"));
                        break;
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
        log.error("用户错误:" + this.mUid + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    //退出群聊
    public void exitGroup(String gid,String token) throws IOException {
        String uid = JwtUtil.getUid(token);
        RespModel respModel = new RespModel();
        respModel.setType("退出群聊");
        int line = mGroupService.deleteGroupByGidUid(gid,uid);
        if(line>0){
            respModel.setMessage("操作成功！");
        }else {
            respModel.setCode(400);
            respModel.setMessage("操作失败！");
        }
        sendMessage(JSONObject.toJSONString(respModel));
    }
    //删除群聊
    public void deleteGroup(String gid,String token) throws IOException {
        String uid = JwtUtil.getUid(token);
        RespModel respModel = new RespModel();
        respModel.setType("删除群聊");
        //首先判断群聊是否存在
        List<None> ids = mGroupService.isExistGroup(gid);
        if(ids==null||ids.size()==0){
            respModel.setCode(202);
            respModel.setMessage("该群聊不存在！");
        }else {
            //判断用户有没有权限
            List<GroupInfo> groupInfoList = mGroupService.findGroupInfoByIdUid(gid,uid);
            if(groupInfoList==null||groupInfoList.size()==0){
                respModel.setCode(202);
                respModel.setMessage("你没有权限操作！");
            }else {
                mMessageInfoService.delValueByKey("gid_"+gid);
                mGroupService.deleteAllJoinGroupByGid(gid);
                mGroupService.deleteGroupInfoByGidUid(gid, uid);
                respModel.setMessage("操作成功！");
            }
        }
        sendMessage(JSONObject.toJSONString(respModel));
    }
    //好友申请
    public void sendApply(String toUid,String token) throws IOException {
        RespModel respModel = new RespModel();
        respModel.setType("好友申请");
        String uid = JwtUtil.getUid(token);
        List<User> users = mUserServer.findFriendByUid(uid);
        List<String> uids = users.stream().map(User::getUid).collect(Collectors.toList());
        if(uids.contains(toUid)){
            respModel.setCode(202);
            respModel.setMessage("该用户已是你的好友，请勿重复添加！");
        }else {
            String nowTime = DateUtil.getNowTime();
            Apply apply = new Apply();

            apply.setUid(uid);
            apply.setToUid(toUid);
            apply.setCreateTime(nowTime);
            apply.setState(false);
            if(mApplyService.sendApply(apply)>0){
                respModel.setCode(200);
                respModel.setMessage("发送成功！");
            }else {
                respModel.setCode(400);
                respModel.setMessage("发送失败！");
            }
        }
        if(webSocketMap.containsKey(toUid)){
            webSocketMap.get(toUid).sendMessage(JSONObject.toJSONString(respModel));
        }
    }
    //同意申请
    public void agreeApply(String id,String toUid,String token) throws IOException {
        RespModel respModel = new RespModel();
        respModel.setType("同意申请");
        String uid = JwtUtil.getUid(token);
        if(mUserServer.findFriendExist(uid,toUid)>0){
            mApplyService.agreeApply(id,uid);
            respModel.setCode(202);
            respModel.setMessage("好友已存在，请勿重复添加！");
        }else {
            if(mApplyService.agreeApply(id,uid)>0){
                if(mUserServer.addFriend(uid,toUid)>0){
                    respModel.setCode(200);
                    List<Object> list = new ArrayList<>();
                    list.add(mUserServer.findByUid(uid));
                    respModel.setListData(list);
                    respModel.setMessage("添加成功！");
                }else {
                    respModel.setCode(400);
                    respModel.setMessage("添加失败！");
                }
            }else {
                respModel.setCode(400);
                respModel.setMessage("添加失败！");
            }
        }
        if(webSocketMap.containsKey(toUid)){
            webSocketMap.get(toUid).sendMessage(JSONObject.toJSONString(respModel));
        }
    }
    //删除好友
    public void deleteFriend(String toUid,String token) throws IOException {
        RespModel respModel = new RespModel();
        respModel.setType("删除好友");
        String uid = JwtUtil.getUid(token);
        if(mUserServer.deleteFriend(uid, toUid)>0){
            respModel.setCode(200);
            respModel.setMessage("添加成功！");
        }else {
            respModel.setCode(400);
            respModel.setMessage("添加失败！");
        }
        if(webSocketMap.containsKey(toUid)){
            webSocketMap.get(toUid).sendMessage(JSONObject.toJSONString(respModel));
        }
    }
    public void sendMessage(String message) throws IOException {
        this.session.getAsyncRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WsOtherHandler.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WsOtherHandler.onlineCount--;
    }

}
