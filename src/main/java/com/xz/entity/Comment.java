package com.xz.entity;

import lombok.Data;

@Data
public class Comment {
    private Integer tcId;
    private String tcUid;
    private String tcText;
    private String tcUsername;
    private String tcAvatar;
    private String tcTime;
}
