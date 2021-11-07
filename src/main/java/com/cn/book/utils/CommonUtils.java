package com.cn.book.utils;

import com.xiaoleilu.hutool.lang.Base64;
import com.xiaoleilu.hutool.util.StrUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    //生成id公共方法
    public String createAllId(){
        String dateString = new SimpleDateFormat("yyyyMMddHHmmssss").format(new Date());
        Object redisNum = redisOperationUtils.get("TABLEIDKEY");
        String resultStr = "";
        if(null==redisNum){
            int initStr = new Random().nextInt(100000);//产生随机数
            redisOperationUtils.set("TABLEIDKEY",String.valueOf(initStr));
        }
        else{
            resultStr = redisNum.toString();
            String putValue = String.valueOf(Double.valueOf(resultStr)+1);
            if(putValue.endsWith(".0")){
                putValue = putValue.substring(0,putValue.length()-2);
            }
            redisOperationUtils.set("TABLEIDKEY",putValue); //redis键值加1
        }
        return StrUtil.hasEmpty(resultStr) ? dateString : dateString+resultStr;
    }

    //用户token创建
    public String createAndUpdateToken(String userId){
        int salt = new Random().nextInt(1000);
        Md5Hash md5Hash = new Md5Hash(userId,String.valueOf(salt),1024);
        String token = md5Hash.toHex();
        int overTime = 24*60*60;//过期时间一天
        redisOperationUtils.set(token,userId,overTime);
        return token;
    }

    //延长token过期时间
    public boolean updateTokenOverTime(String token){
        int overTime = 24*60*60;//每调一次，过期时间延长一天
        boolean flag = redisOperationUtils.set(token,1,overTime);
        return flag;
    }

    //本地图片转base64编码
    public String imageToBase64(String imagePath) throws Exception{
        String base64Str = "";
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imagePath);
            System.out.println("文件大小（字节）="+in.available());
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 对字节数组进行Base64编码，得到Base64编码的字符串
//        BASE64Encoder encoder = new BASE64Encoder();
//        String base64Str = encoder.encode(data);
        base64Str = Base64.encode(data);
        return base64Str;
    }


}
