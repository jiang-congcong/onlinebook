package com.cn.book.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    void addBook(Map<String,Object> reqMap) throws Exception;

    void insertBookSku(Map<String,Object> reqMap) throws Exception;

    void insertBookSkuRl(Map<String,Object> reqMap) throws Exception;

    void insertBookPicture(Map<String,Object> reqMap) throws Exception;

    void insertBigBookPicture(Map<String,Object> reqMap) throws Exception;

    void insertBookSmall(Map<String,Object> reqMap) throws Exception;

    void insertBookShopRL(Map<String,Object> reqMap) throws Exception;

    void insertBookCatgRL(Map<String,Object> reqMap) throws Exception;

    void operateBookValidState(@Param("reqMap")Map<String,Object> reqMap,@Param("mcdsIdList")List<String> mcdsIdList) throws Exception;

    void updatePicValidState(Map<String,Object> reqMap) throws Exception;

    void updateBookInfo(Map<String,Object> reqMap) throws Exception;

    List<Map<String, Object>> queryAllCatg() throws Exception;

}
