package com.cn.book.iservice;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/4 20:06
 */
public interface IReceiverSV {

    List<Map<String,Object>> queryReceiverList(Map<String,Object> reqMap) throws Exception;

    boolean addReceiver(Map<String,Object> reqMap) throws Exception;

    boolean delReceiverInfo(Map<String,Object> reqMap) throws Exception;

    boolean updateReceiverInfo(Map<String,Object> reqMap) throws Exception;
}
