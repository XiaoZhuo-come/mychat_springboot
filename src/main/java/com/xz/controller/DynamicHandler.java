package com.xz.controller;

import com.xz.entity.DynamicInfo;
import com.xz.entity.None;
import com.xz.entity.RespModel;
import com.xz.server.impl.DynamicServiceImpl;
import com.xz.util.DateUtil;
import com.xz.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/dynamic")
public class DynamicHandler {
    @Autowired
    private DynamicServiceImpl mDynamicServer;

    @GetMapping("/getMyDynamic")
    public List<DynamicInfo> getMyDynamic(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        Claims claims = JwtUtil.parseJWT(token);
        String myUid = (String) claims.get("uid");
        return mDynamicServer.getMyDynamic(myUid);
    }

    @GetMapping("/getMyselfDynamic")
    public List<DynamicInfo> getMyselfDynamic(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        Claims claims = JwtUtil.parseJWT(token);
        String myUid = (String) claims.get("uid");
        return mDynamicServer.getMyselfDynamic(myUid);
    }

    @GetMapping("/likeDynamic")
    public RespModel likeDynamic(String did, @RequestHeader("Authorization") String token){
        RespModel respModel = new RespModel();
        respModel.setType("动态点赞");
        String uid = JwtUtil.getUid(token);
        List<None> noneList = mDynamicServer.isLikeDynamic(uid,did);

        if(dynamicIsExist(did)){
            if(noneList!=null&&noneList.size()>0){
                respModel.setCode(202);
                respModel.setMessage("请勿重复点赞！");
            }else {
                if(mDynamicServer.likeDynamic(uid,did)>0){
                    respModel.setMessage("点赞成功！");
                }else {
                    respModel.setCode(400);
                    respModel.setMessage("操作失败！");
                }
            }
        }else {
            respModel.setCode(400);
            respModel.setMessage("操作失败，动态已不存在！");
        }

        return respModel;
    }

    @GetMapping("/cancelLikeDynamic")
    public RespModel cancelLikeDynamic(String did, @RequestHeader("Authorization") String token){
        String uid = JwtUtil.getUid(token);
        RespModel respModel = new RespModel();
        respModel.setType("取消点赞");

        if(dynamicIsExist(did)){
            if(mDynamicServer.cancelLikeDynamic(uid,did)>0){
                respModel.setMessage("操作成功！");
            }else {
                respModel.setMessage("操作失败！");
                respModel.setCode(400);
            }
        }else {
            respModel.setCode(400);
            respModel.setMessage("操作失败，动态已不存在！");

        }

        return respModel;
    }

    @GetMapping("/addComment")
    public RespModel addComment(String did,String content,HttpServletRequest request){
        RespModel respModel = new RespModel();
        String nowTime = DateUtil.getNowTime();
        String token = request.getHeader("Authorization");
        Claims claims = JwtUtil.parseJWT(token);
        String myUid = (String) claims.get("uid");


        if(dynamicIsExist(did)){
            int line = mDynamicServer.addComment(myUid,did,content,nowTime);
            if(line>0){
                respModel.setMessage("操作成功！");
            }
        }else {
            respModel.setCode(400);
            respModel.setMessage("操作失败，动态已不存在！");
        }
        return respModel;
    }

    @GetMapping("/addDynamic")
    public int addDynamic(String content,String img,HttpServletRequest request){
        String nowTime = DateUtil.getNowTime();
        String token = request.getHeader("Authorization");
        Claims claims = JwtUtil.parseJWT(token);
        String myUid = (String) claims.get("uid");

        return mDynamicServer.addDynamic(myUid,nowTime,content,img,"0");
    }

    @GetMapping("/deleteMyselfDynamic")
    public RespModel deleteMyselfDynamic(String did, @RequestHeader("Authorization") String token){
        String uid = JwtUtil.getUid(token);
        RespModel respModel = new RespModel();
        respModel.setType("删除动态");
        List<None> noneList = mDynamicServer.selectDynamicByUidId(uid,did);
        if(noneList==null||noneList.size()==0){
            respModel.setCode(202);
            respModel.setMessage("你没有权限操作！");
            return respModel;
        }
        //先删除评论
        mDynamicServer.deleteCommentByDid(did);
        //先删除点赞
        mDynamicServer.deleteDynamicLikeByDid(did);
        if(mDynamicServer.deleteMyselfDynamic(uid,did)>0){
            respModel.setMessage("操作成功！");
        }else{
            respModel.setCode(400);
            respModel.setMessage("操作失败！");
        }

        return respModel;
    }


    private boolean dynamicIsExist(String did){
        List<None> noneList = mDynamicServer.selectDynamicById(did);
        return noneList != null && noneList.size() != 0;
    }
}
