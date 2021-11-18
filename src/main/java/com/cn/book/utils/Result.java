package com.cn.book.utils;

import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/13 12:53
 */
@Configuration
public class Result implements Serializable {

    private String rtnCode;
    private String rtnMessage;
    private Map<String,Object> result;

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getRtnMessage() {
        return rtnMessage;
    }

    public void setRtnMessage(String rtnMessage) {
        this.rtnMessage = rtnMessage;
    }

    public Map<String, Object> getResult() {
        return result;
    }

    public void setResult(Map<String, Object> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "rtnCode='" + rtnCode + '\'' +
                ", rtnMessage='" + rtnMessage + '\'' +
                ", result=" + result +
                '}';
    }
}
