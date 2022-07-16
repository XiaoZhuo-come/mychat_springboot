package com.xz.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileHandler {
    private static List<String> mFileType = new ArrayList<>(Arrays.asList("png","jpg","jpeg"));

    @PostMapping("/upload")
    public String upload(MultipartFile file){
        ApplicationHome applicationHome = new ApplicationHome(FileHandler.class);
        //获取jar包所在目录
        ApplicationHome h = new ApplicationHome(getClass());
        File jarF = h.getSource();
        //在jar包所在目录下生成一个upload文件夹用来存储上传的图片
        String parent = jarF.getParentFile().toString()+"/img/";
        JSONObject jsonObject = new JSONObject();
        String fileName="";
        if(file.getSize()>0){
            fileName = "img_"+String.valueOf(System.currentTimeMillis());
            int index = file.getOriginalFilename().lastIndexOf(".");
            String fileType = file.getOriginalFilename().substring(index+1,file.getOriginalFilename().length());
            fileName+="."+fileType;
            if(mFileType.contains(fileType)){
                File fileImg=new File(parent, fileName);
                try {
                    file.transferTo(fileImg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                jsonObject.put("message","不支持的图片格式");
            }
        }else {
            jsonObject.put("message","不能上传空文件");
        }
        jsonObject.put("data","/img/"+fileName);
        return jsonObject.toJSONString();
    }
}
