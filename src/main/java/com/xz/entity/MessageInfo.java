package com.xz.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageInfo implements Serializable {
    private String fromUid;
    private String fromName;
    private String fromGName;
    private String fromAvatar;
    private String toUid;
    private String toGid;
    private String message;
}
