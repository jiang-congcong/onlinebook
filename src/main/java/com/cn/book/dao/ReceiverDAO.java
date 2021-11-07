package com.cn.book.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/4 20:07
 */
@Mapper
public interface ReceiverDAO {

    List<Map<String,Object>> queryReceiverList(Map<String,Object> reqMap) throws Exception;

    void addReceiverInfo(Map<String,Object> reqMap) throws Exception;

    void delReceiverInfo(Map<String,Object> reqMap) throws Exception;

    void updateReceiverInfo(Map<String,Object> reqMap) throws Exception;

    List<String> queryHasDefaultAddress(String userId) throws Exception;

}
