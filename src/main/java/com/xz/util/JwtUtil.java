package com.xz.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.xz.entity.User;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class JwtUtil {

    private static final String KEY = "mynameisxiaozhuo";
    private static final long EXPIRE = 1000 * 60 * 60 * 24;
    //    @Value("${jwt.secret}")
    //    private static String key;
    /**
     * 用户登录成功后生成Jwt
     * 使用Hs256算法  私匙使用用户密码
     *
     * @param user      登录成功的user对象
     * @return
     */
    public static String createJWT(User user) {
        //指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        Date dateExpire = new Date(System.currentTimeMillis()+EXPIRE) ;
        System.out.println(now);
        System.out.println(dateExpire);

        //创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("id", user.getId());
        claims.put("uid",user.getUid());
        claims.put("avatar",user.getAvatar()==null?"":user.getAvatar());
        claims.put("username", user.getUsername());

        //生成签名的时候使用的秘钥secret,这个方法本地封装了的，一般可以从本地配置文件中读取，切记这个秘钥不能外露哦。它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
        //生成签发人
        String subject = user.getUsername();

        //下面就是在为payload添加各种标准声明和私有声明了
        //这里其实就是new一个JwtBuilder，设置jwt的body
        JwtBuilder builder = Jwts.builder()
                //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                //iat: jwt的签发时间
                .setIssuedAt(now)

                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUID.randomUUID().toString())
                //代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(subject)
                //设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, KEY);

        return builder.compact();
    }


    /**
     * Token的解密
     * @param token 加密后的token
     * @return
     */
    public static Claims parseJWT(String token) {
        //签名秘钥，和生成的签名的秘钥一模一样

        //得到DefaultJwtParser
        Claims claims = Jwts.parser()
                //设置签名的秘钥
                .setSigningKey(KEY)
                //设置需要解析的jwt
                .parseClaimsJws(token).getBody();
        return claims;
    }


    /**
     * 校验token
     * 在这里可以使用官方的校验，我这里校验的是token中携带的密码于数据库一致的话就校验通过
     * @param token
     * @return
     */
    public static String isVerify(String token) {
        if(StringUtils.isBlank(token)){
            return "token不能为空";
        }
        //签名秘钥，和生成的签名的秘钥一模一样
        //Jwts.parser在执行parseClaimsJws(token)时如果token时间过期会抛出ExpiredJwtException异常
        try {
            //得到DefaultJwtParser
            Claims claims = Jwts.parser()
                    //设置签名的秘钥
                    .setSigningKey(KEY)
                    //设置需要解析的jwt
                    .parseClaimsJws(token).getBody();
//            String uid = (String) claims.get("uid");
//            String username = (String) claims.get("username");

            return "OK";
        }catch (SignatureVerificationException e) {
            return "无效签名";
        } catch (TokenExpiredException e) {
            return "token过期";
        } catch (AlgorithmMismatchException e) {
            return "token算法不一致！";
        } catch (Exception e) {
            return "token无效";
        }

    }

    public static String getUid(String token){
        String res = isVerify(token);
        if(res.equals("OK")){
            Claims claims = parseJWT(token);
            return (String) claims.get("uid");
        }else {
            return res;
        }

    }

}