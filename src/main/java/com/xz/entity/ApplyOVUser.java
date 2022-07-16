package com.xz.entity;

import lombok.Data;

@Data
public class ApplyOVUser {
    private Integer id;
    private String uid;
    private String toUid;
    private String createTime;
    private Integer state;
    private String username;
    private String avatar;

}
