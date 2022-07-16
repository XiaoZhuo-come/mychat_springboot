package com.xz.controller;


import com.alibaba.fastjson.JSONObject;
import com.xz.entity.GroupInfo;
import com.xz.entity.None;
import com.xz.entity.RespModel;
import com.xz.server.impl.GroupServiceImpl;
import com.xz.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupHandler {
    @Autowired
    private GroupServiceImpl mGroupService;

    @GetMapping("/getMyGroup")
    public String getMyGroup(HttpServletRequest request){

        String token = request.getHeader("Authorization");
        Claims claims = JwtUtil.parseJWT(token);
        String myUid = (String) claims.get("uid");
        List<GroupInfo> groupInfoList = mGroupService.findGroupByUid(myUid);
        return JSONObject.toJSONString(groupInfoList);
    }

    @GetMapping("/findGroupByGid")
    public RespModel findGroupByGid(String gid){
        RespModel respModel = new RespModel();
        GroupInfo groupInfo = mGroupService.findGroupByGid(gid);
        if(groupInfo!=null){
            List<Object> list = new ArrayList<>();
            list.add(groupInfo);
            respModel.setListData(list);
            respModel.setMessage("操作成功！");
        }else {
            respModel.setCode(400);
            respModel.setMessage("没有搜索相关群聊！");
        }
        return respModel;
    }

    @PostMapping("/addGroup")
    public RespModel addGroup(@RequestBody GroupInfo groupInfo,@RequestHeader("Authorization")  String token){
        String uid = JwtUtil.getUid(token);
        groupInfo.setUid(uid);
        List<None> list = mGroupService.isExistGroup(groupInfo.getGid());
        RespModel respModel = new RespModel();

        if(list!=null&&list.size()>0){
            respModel.setCode(202);
            respModel.setMessage("该群聊ID已被使用！");
        }else {
            int line = mGroupService.addGroup(groupInfo);
            if(line>0){
                List<Object> listData = new ArrayList<>();
                mGroupService.joinGroup(groupInfo.getGid(),uid);
                listData.add(groupInfo);
                respModel.setListData(listData);
                respModel.setMessage("操作成功！");
            }else {
                respModel.setMessage("创建失败！");
            }
        }
        return respModel;
    }

    @GetMapping("/joinGroup")
    public RespModel joinGroup(String gid, @RequestHeader("Authorization")  String token){
        String uid = JwtUtil.getUid(token);
        List<None> list = mGroupService.isJoinGroup(gid,uid);
        RespModel respModel = new RespModel();
        if(list!=null&&list.size()>0){
            respModel.setCode(202);
            respModel.setMessage("你已经加入了该群聊，请勿重复加入！");
        }else {
            respModel.setMessage("失败！");
            if(mGroupService.joinGroup(gid, uid)>0){
                GroupInfo groupInfo = mGroupService.findGroupByGid(gid);
                List<Object> listData = new ArrayList<>();
                listData.add(groupInfo);
                respModel.setListData(listData);
                respModel.setCode(200);
                respModel.setMessage("加入成功！");
            }
        }

        return respModel;
    }

    @PostMapping("/updateGroup")
    public RespModel updateGroup(@RequestBody GroupInfo groupInfo,@RequestHeader("Authorization")  String token){
        String uid = JwtUtil.getUid(token);
        RespModel respModel = new RespModel();
        respModel.setType("修改群聊");
        String gid = groupInfo.getGid();
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
                int line = mGroupService.updateGroup(groupInfo);
                if(line>0){
                    respModel.setMessage("修改成功！");
                }else {
                    respModel.setMessage("修改失败！");
                }
            }
        }
        return respModel;
    }

}
