package com.cn.book.iservice;

import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/3 20:31
 */
public interface ICartSV {

    Map<String,Object> queryCartListByUserId(Map<String,Object> reqMap);

}
