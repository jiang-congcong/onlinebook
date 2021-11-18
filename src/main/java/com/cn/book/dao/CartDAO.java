package com.cn.book.dao;

import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/3 20:32
 */
@Mapper
public interface CartDAO {

    List<Map<String,Object>> queryCartListByUserId(Map<String,Object> reqMap);

    int queryCartListByUserIdTotal(Map<String,Object> reqMap);

    void insertNewCart(Map<String,Object> reqMap);
    void updateCartBook(Map<String,Object> reqMap);
    void deleteCartBook(Map<String,Object> reqMap);
    String selectCartBookNum(String cartId);

    List<String> queryCartId(Map<String,Object> reqMap);

}
