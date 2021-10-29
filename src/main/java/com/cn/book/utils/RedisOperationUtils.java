package com.cn.book.utils;

import com.xiaoleilu.hutool.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author jiangcongcong
 * @date 2021/10/28 20:19
 */
@Component
public class RedisOperationUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Object get(String key) { //获取缓存值
        Object resultObj = String.valueOf(new Random().nextInt(100000));
        if(!StrUtil.hasEmpty(key)){
            if(null!=redisTemplate.opsForValue().get(key)){
                resultObj = redisTemplate.opsForValue().get(key);
                double d = Double.parseDouble(resultObj.toString())+1; //递增
                set(key,d);
            }
            else {
                set(key,1000001); //初始化
            }
        }
        return resultObj;
    }

    public boolean set(String key, Object value) { //设置缓存值
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //删除缓存
    public void del(String key) {
        if (key != null && key.length() > 0) {
            redisTemplate.delete(key);
        }
    }

}
