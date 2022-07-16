package com.xz.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

@Data
public class DynamicInfo {
    private Integer id;
    private String tdText;
    private String tdImg;
    private String tdTime;
    private String tdUsername;
    private String tdAvatar;

    @TableField(exist = false)
    private List<Comment> commentList;

    @TableField(exist = false)
    private List<String> likeList;
}
