package com.xz.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorHandler {
    @GetMapping("/token")
    public String tokenNone(String msg){
        return "{\"message\": \""+msg+"\"}";
    }
}
