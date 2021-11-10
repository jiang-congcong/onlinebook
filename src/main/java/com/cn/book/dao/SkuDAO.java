package com.cn.book.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/10 18:52
 */
@Mapper
public interface SkuDAO {

    void operationBookSku(Map<String, Object> reqMap) throws Exception;

    Map<String,Object> queryBookSku(String bookId) throws Exception;

}
