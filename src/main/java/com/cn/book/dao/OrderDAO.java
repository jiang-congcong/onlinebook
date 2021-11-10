package com.cn.book.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/5 19:38
 */
@Mapper
public interface OrderDAO {

    void insertOrderInfo(@Param("reqList") List<Map<String,Object>> reqList) throws Exception;

    List<Map<String,Object>> queryOrderInfo(Map<String,Object> reqMap) throws Exception;

    void updateOrderState(Map<String,Object> reqMap) throws Exception;

    List<Map<String,Object>> queryUserOrderList(Map<String,Object> reqMap) throws Exception;

    int queryUserOrderListTotal(Map<String,Object> reqMap) throws Exception;

    void updateBookSku(Map<String,Object> reqMap) throws Exception;

    List<Map<String,Object>> selectBookSku(@Param("bookIdList") List<String> bookId) throws Exception;

    void deleteCartInfo(Map<String,Object> reqMap) throws Exception;

    void updateOrderInfo(Map<String,Object> reqMap) throws Exception;
}
