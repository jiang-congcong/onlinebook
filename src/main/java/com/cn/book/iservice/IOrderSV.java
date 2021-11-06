package com.cn.book.iservice;

import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/5 19:37
 */
public interface IOrderSV {

    boolean insertOrderInfo(List<Map<String,Object>> reqList) throws Exception;

    Map<String,Object> queryOrderInfo(Map<String,Object> reqMap) throws Exception;

    boolean updateOrderState(Map<String,Object> reqMap) throws Exception;

    Map<String,Object> queryUserOrderList(Map<String,Object> reqMap) throws Exception;

}
