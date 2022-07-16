package com.xz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

@TableName("t_apply")
@Data
public class Apply {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String uid;
    private String toUid;
    private String createTime;
    private Boolean state;
}
