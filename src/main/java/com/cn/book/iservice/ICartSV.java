package com.cn.book.iservice;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/3 20:31
 */
public interface ICartSV {

    Map<String,Object> queryCartListByUserId(Map<String,Object> reqMap);

    boolean addCart(Map<String,Object> reqMap) throws Exception;

    boolean deleteCart(@RequestBody Map<String,Object> reqMap) throws Exception;

}
