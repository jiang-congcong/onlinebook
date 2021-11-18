package com.cn.book.iservice;


import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/10/31 9:44
 */
public interface IBookSV {

    Map<String,Object> queryBookList(Map<String,Object> reqMap) throws Exception;

    List<Map<String,Object>> queryHomePage() throws Exception;

    Map<String,Object> queryBookDetail(String bookId) throws Exception;

    boolean addBook(Map<String,Object> reqMap) throws Exception;

    void operateBookValidState(Map<String,Object> reqMap) throws Exception;

    void updateBook(Map<String,Object> reqMap) throws Exception;

    List<Map<String, Object>> queryAllCatg() throws Exception;

}
