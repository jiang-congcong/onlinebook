package com.cn.book.iservice;

import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/10 18:52
 */
public interface ISkuSV {

    void addBookSku(Map<String, Object> reqMap) throws Exception;

    void cutBookStock(Map<String, Object> reqMap) throws Exception;
}
