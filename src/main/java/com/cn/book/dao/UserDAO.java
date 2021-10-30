package com.cn.book.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/10/27 19:39
 */
@Mapper
public interface UserDAO {

    void register(Map<String,Object> reqMap) throws Exception;

    List<Map<String,Object>> queryUserPassword(String userId) throws Exception;

    List<String> checkUsernameIsRegister(String username) throws Exception;
}
