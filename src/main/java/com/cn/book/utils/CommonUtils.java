package com.cn.book.utils;

import com.xiaoleilu.hutool.util.StrUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author jiangcongcong
 * @date 2021/10/28 19:49
 */
@Component
public class CommonUtils {

    @Autowired
    private RedisOperationUtils redisOperationUtils;

    //MD5+加盐 对密码进行加密
    public String passwordMD5HashSalt(String password, String salt, int hashTime){
        String resultStr = "";
        if(StrUtil.hasEmpty(password)||StrUtil.hasEmpty(salt)){
            return resultStr;
        }
        Md5Hash md5Hash = new Md5Hash(password,salt,hashTime);
        resultStr = md5Hash.toHex();
        return resultStr;
    }

    public String createAllId(){
        String dateString = new SimpleDateFormat("yyyyMMddHHmmssss").format(new Date());
        String redisNum = redisOperationUtils.get("TABLEIDKEY").toString();
        return StrUtil.hasEmpty(redisNum) ? dateString : dateString+redisNum;
    }

    //用户token创建
    public String createAndUpdateToken(String userId){
        int salt = new Random().nextInt(1000);
        Md5Hash md5Hash = new Md5Hash(userId,salt,1024);
        String token = md5Hash.toHex();
        int overTime = 24*60*60;//过期时间一天
        redisOperationUtils.set(token,1,overTime);
        return token;
    }

    //延长token过期时间
    public boolean updateTokenOverTime(String token){
        int overTime = 24*60*60;//每调一次，过期时间延长一天
        boolean flag = redisOperationUtils.set(token,1,overTime);
        return flag;
    }


}
