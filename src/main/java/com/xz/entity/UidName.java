package com.xz.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_group_info")
public class UidName {
    private Integer id;
    private String uid;
    private String username;
}
