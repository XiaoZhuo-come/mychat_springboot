package com.xz.entity;

import lombok.Data;

import java.util.List;

@Data
public class RespModel {
    private Integer code=200;
    private String type;
    private String message;
    private String token;
    private List<Object> listData;
}
