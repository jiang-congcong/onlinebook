package com.cn.book.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/10/31 9:45
 */
@Mapper
public interface BookDAO {

    List<Map<String,Object>> queryBookList(Map<String,Object> reqMap) throws Exception;

    int queryBookListTotal(Map<String,Object> reqMap) throws Exception;

    List<Map<String,Object>> queryAllCatgId() throws Exception;

    List<String> queryBookImageSmall(String bookId) throws Exception;

    List<String> queryBookBigPic(String bookId) throws Exception;

}
