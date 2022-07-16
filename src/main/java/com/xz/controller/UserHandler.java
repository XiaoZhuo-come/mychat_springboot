package com.xz.controller;

import com.alibaba.fastjson.JSONObject;
import com.xz.entity.*;
import com.xz.server.impl.ApplyServiceImpl;
import com.xz.server.impl.UserServiceImpl;
import com.xz.util.DateUtil;
import com.xz.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserHandler {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserServiceImpl mUserServer;
    @Autowired
    private ApplyServiceImpl mApplyService;



    @GetMapping("/getMyFriend")
    public String getMyFriend(HttpServletRequest request){

        String token = request.getHeader("Authorization");
        Claims claims = JwtUtil.parseJWT(token);
        String myUid = (String) claims.get("uid");
        List<User> users = mUserServer.findFriendByUid(myUid);
        return JSONObject.toJSONString(users);
    }


    @PostMapping("/login")
    public String login(@RequestBody User user,HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(user.getUid())||StringUtils.isBlank(user.getPassword())){
            return "{\"message\": \"账号和密码不能为空\"}";
        }
        map.put("uid",user.getUid());
        map.put("password",user.getPassword());

        List<User> users = mUserServer.selectByMap(map);

        if (users==null||users.size()==0){
            return "{\"message\": \"账号不存在或密码错误\"}";
        }else {
            User myUser = users.get(0);
            String token = "";
            String tokenKey = "token_"+user.getUid();
            //如果是首次登陆
            if(!redisTemplate.hasKey(tokenKey)){
                token = JwtUtil.createJWT(myUser);
                redisTemplate.opsForValue().set(tokenKey,token);
                return "{\"token\": \""+token+"\"}";
            }else {
                token = redisTemplate.opsForValue().get(tokenKey).toString();
                //token为验证无法通过则更新token
                if(!JwtUtil.isVerify(token).equals("OK")){
                    token = JwtUtil.createJWT(myUser);
                    redisTemplate.opsForValue().set(tokenKey,token);
                }
                //String token = JwtUtil.createJWT(users.get(0));
                return "{\"token\": \""+token+"\"}";
            }

        }

    }
    @GetMapping("/getUserInfo")
    public String getUserinfo(@RequestHeader("Authorization") String token){
        JSONObject jsonObject = new JSONObject();

        if(StringUtils.isBlank(token)){
            jsonObject.put("message","token不能为空");
            return jsonObject.toJSONString();
        }
        Claims claims = JwtUtil.parseJWT(token);
        User user = mUserServer.findByUid(claims.get("uid").toString());
        String strData = String.format("{\n" +
                "    \"id\": %d,\n" +
                "    \"uid\": \"%s\",\n" +
                "    \"username\": \"%s\",\n" +
                "    \"avatar\": \"%s\"\n" +
                "}",user.getId(),user.getUid(),user.getUsername(),user.getAvatar());
        return strData;
    }

    @PostMapping("/addUser")
    public String addUser(@RequestBody User user){
        String message = "注册失败";
        if(mUserServer.findByUidCount(user.getUid())>0){
            return "该账号ID已存在";
        }else {
            if(mUserServer.addUser(user)>0){

                message = "注册成功";
            }
            return message;
        }
    }

    @PostMapping("/updateInfo")
    public String updateUserInfo(@RequestBody UserRequest userRequest, @RequestHeader("Authorization") String token){
        User user = new User();
        user.setUid(JwtUtil.getUid(token));
        if (StringUtils.isNotBlank(userRequest.getAvatar())) {
            user.setAvatar(userRequest.getAvatar());
        }
        if (StringUtils.isNotBlank(userRequest.getUsername())) {
            user.setUsername(userRequest.getUsername());
        }
        if(mUserServer.updateUser(user)>0){
            return "修改成功";
        }
        return "修改失败";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestBody UserRequest userRequest,@RequestHeader("Authorization") String token){
        String uid = JwtUtil.getUid(token);
        if(mUserServer.findByUidAndPwdCount(uid,userRequest.getOldPassword())>0){
            User user = new User();
            user.setUid(JwtUtil.getUid(token));
            if (StringUtils.isNotBlank(userRequest.getPassword())) {
                user.setPassword(userRequest.getPassword());
            }
            if(mUserServer.updateUser(user)>0){
                return "修改成功";
            }
            return "修改失败";
        }else {
            return "旧密码失败";
        }

    }

    @GetMapping("/findByUid")
    public User findByUid(String uid){
        User user = mUserServer.findByUid(uid);
        return user;
    }

    @GetMapping("/sendApply")
    public String sendApply(String toUid,@RequestHeader("Authorization") String token){
        String uid = JwtUtil.getUid(token);
        List<User> users = mUserServer.findFriendByUid(uid);
        List<String> uids = users.stream().map(User::getUid).collect(Collectors.toList());
        if(uids.contains(toUid)){
            return "该用户已是你的好友，请勿重复添加！";
        }else {
            String nowTime = DateUtil.getNowTime();
            Apply apply = new Apply();

            apply.setUid(uid);
            apply.setToUid(toUid);
            apply.setCreateTime(nowTime);
            apply.setState(false);
            if(mApplyService.sendApply(apply)>0){
                return "发送成功！";
            }else {
                return "发送失败！";
            }
        }
    }

    @GetMapping("/agreeApply")
    public String agreeApply(String id,String toUid,@RequestHeader("Authorization") String token){
        String uid = JwtUtil.getUid(token);
        if(mUserServer.findFriendExist(uid,toUid)>0){
            mApplyService.agreeApply(id,uid);
            return "好友已存在，请勿重复添加！";
        }else {
            if(mApplyService.agreeApply(id,uid)>0){
                if(mUserServer.addFriend(uid,toUid)>0){
                    return "添加成功！";
                }else {
                    return "添加失败！";
                }
            }else {
                return "添加失败！";
            }
        }

    }

    @GetMapping("/getMyApply")
    public List<ApplyOVUser> getMyApply(@RequestHeader("Authorization") String token){
        String uid = JwtUtil.getUid(token);
        return mApplyService.getMyApply(uid);
    }

    @GetMapping("/deleteFriend")
    public String deleteFriend(String toUid,@RequestHeader("Authorization") String token){
        String uid = JwtUtil.getUid(token);
        if(mUserServer.deleteFriend(uid, toUid)>0){
            return "删除成功！";
        }else {
            return "删除失败！";
        }

    }


}
