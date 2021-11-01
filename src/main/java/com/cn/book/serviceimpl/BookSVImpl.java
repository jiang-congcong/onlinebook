package com.cn.book.serviceimpl;

import com.cn.book.dao.BookDAO;
import com.cn.book.iservice.IBookSV;
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
                Map<String, Object> reqMaq = new HashMap<>();
                reqMaq.put("start", 0);
                reqMaq.put("limit", 4);
                reqMaq.put("catgId", catgId);
                List<Map<String, Object>> queryBookList = bookDAO.queryBookList(reqMaq);
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

}
