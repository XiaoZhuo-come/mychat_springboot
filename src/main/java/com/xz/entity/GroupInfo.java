package com.xz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_group_info")
public class GroupInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String gid;
    private String uid;
    private String avatar;
}
