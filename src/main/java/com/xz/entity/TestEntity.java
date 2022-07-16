package com.xz.entity;

import lombok.Data;

@Data
public class TestEntity {
    private Integer id;
    private Integer blogId;
    private String content;
    private String createTime;
    private Integer userId;
}
