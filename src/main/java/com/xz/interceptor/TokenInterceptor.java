package com.xz.interceptor;

import com.xz.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TokenInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(HandlerInterceptor.class);

    //不拦截的url
    private static List<String> mSkip = new ArrayList<>(
            Arrays.asList("/user/login","/error/token","/error","/file/upload","/img")
    );
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //请求地址
        String requestUrl = request.getServletPath();
        logger.info("url:"+requestUrl);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length, Authorization, Accept,X-Requested-With");
        String method= request.getMethod();
        if (method.equals("OPTIONS")){
            return false;
        }

        //从请求头中获取用户token（登陆凭证根据业务而定）
//        boolean tmp = mSkip.contains(requestUrl);
//        if(!mSkip.contains(requestUrl)||!requestUrl.startsWith("/img")){
//
//        }

        //request.getHeader(String) 从请求头中获取数据
        String token = request.getHeader("Authorization");
        String info = JwtUtil.isVerify(token);
        info = URLEncoder.encode(info,"utf8");
        if(info.equals("OK")){
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }else {
            //request.setAttribute("msg",info);
            //request.getRequestDispatcher("/error/token?msg="+info).forward(request,response);
            response.sendRedirect("/error/token?msg="+info);
            return false;
        }
        //return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}
