package com.xz.entity;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String avatar;
    private String password;
    private String oldPassword;
}
