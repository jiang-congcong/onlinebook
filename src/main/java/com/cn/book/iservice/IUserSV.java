package com.cn.book.iservice;

import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/10/27 19:36
 */
public interface IUserSV {

    void register(Map<String,Object> reqMap) throws Exception;

    Map<String,Object> queryUserPasswordAndSalt(String userId) throws Exception;

    boolean checkUsernameIsRegister(String username) throws Exception;
}
