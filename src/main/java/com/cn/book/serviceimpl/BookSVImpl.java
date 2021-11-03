package com.cn.book.serviceimpl;

import com.cn.book.controller.UserController;
import com.cn.book.dao.BookDAO;
import com.cn.book.iservice.IBookSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/10/31 9:45
 */
@Service
public class BookSVImpl implements IBookSV {

    public static Logger logger = LoggerFactory.getLogger(BookSVImpl.class);

    @Autowired
    private BookDAO bookDAO;

    @Override
    public Map<String,Object> queryBookList(Map<String,Object> reqMap) throws Exception{
        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();
        int total = 0;
        if(null!=reqMap&&reqMap.size()>0){
            total = bookDAO.queryBookListTotal(reqMap);
            if(total>0) {
                List<Map<String, Object>> midList = bookDAO.queryBookList(reqMap);
                if (null != midList && midList.size() > 0) {
                    for(Map<String,Object> eachMap:midList){
                        eachMap.put("describe",eachMap.get("introduce"));
                        double d1 = (Double) eachMap.get("skuTotal");//原始库存总量
                        double d2 = (Double) eachMap.get("skuSale");//销量
                        eachMap.put("skuNum",d1-d2); //剩余库存
                    }
                    resultList = midList;
                }
            }
        }
        resultMap.put("total",total);
        resultMap.put("bookList",resultList);
        return resultMap;
    }

    @Override
     public List<Map<String,Object>> queryHomePage() throws Exception{
        List<Map<String,Object>> resultList = new ArrayList<>();
        List<Map<String,Object>> allCatgId = bookDAO.queryAllCatgId();
        if(null!=allCatgId&&allCatgId.size()>0){
            for(Map<String,Object> eachMap:allCatgId) {
                String catgId = (String)eachMap.get("catgId");
                Map<String, Object> reqMap = new HashMap<>();
                reqMap.put("start", 0);
                reqMap.put("limit", 4);
                reqMap.put("catgId", catgId);
                List<Map<String, Object>> queryBookList = bookDAO.queryBookList(reqMap);
                if(null!=queryBookList&&queryBookList.size()>0){
                    Map<String,Object> eachResultMap = new HashMap<>();
                    eachResultMap.put("type",eachMap.get("catgName"));
                    eachResultMap.put("data",queryBookList);
                    resultList.add(eachResultMap);
                }
            }
        }
        return resultList;
    }

    @Override
    public Map<String,Object> queryBookDetail(String bookId) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put("bookId",bookId);
        reqMap.put("start", 0);
        reqMap.put("limit", 10);
        List<Map<String, Object>> queryBookList = new ArrayList<>();
        try {
            queryBookList = bookDAO.queryBookList(reqMap);
        }catch (Exception e){
            logger.error("查询图书详情失败");
        }
        if(null!=queryBookList&&queryBookList.size()>0){
            resultMap = queryBookList.get(0);
            List<String> bookImageSmall = new ArrayList<>();
            List<String> bookBigImg = new ArrayList<>();
            try {
                bookImageSmall = bookDAO.queryBookImageSmall(bookId);
                bookBigImg = bookDAO.queryBookBigPic(bookId);
            }catch (Exception e){
                logger.error("查询图书图片组合或大图失败");
            }
            resultMap.put("imageSmall",bookImageSmall);
            if(null!=bookBigImg&&bookBigImg.size()>0){
                resultMap.put("detail",bookBigImg.get(0));
            }
            else{
                resultMap.put("detail","");
            }
        }
        return resultMap;
    }



}
