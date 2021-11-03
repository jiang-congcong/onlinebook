package com.cn.book.serviceimpl;

import com.cn.book.dao.CartDAO;
import com.cn.book.iservice.ICartSV;
import com.xiaoleilu.hutool.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/3 20:32
 */

@Service
public class CartSVImpl implements ICartSV {

    public static Logger logger = LoggerFactory.getLogger(CartSVImpl.class);

    @Autowired
    private CartDAO cartDAO;

    @Override
    public Map<String,Object> queryCartListByUserId(Map<String,Object> reqMap){
        Map<String,Object> resultMap = new HashMap<>();
        List<Map<String,Object>> resultList = new ArrayList<>();
        int total = 0;
        if(null!=reqMap){
            total = cartDAO.queryCartListByUserIdTotal(reqMap);
            if(total>0) {
                try {
                    resultList = cartDAO.queryCartListByUserId(reqMap);
                    for (Map<String, Object> eachMap : resultList) {
                        eachMap.put("describe", eachMap.get("introduce"));
                        double d1 = (Double) eachMap.get("skuTotal");//原始库存总量
                        double d2 = (Double) eachMap.get("skuSale");//销量
                        eachMap.put("skuNum", d1 - d2); //剩余库存
                    }
                } catch (Exception e) {
                    logger.error("查询购物车列表失败！");
                }
            }
        }
        resultMap.put("total",total);
        resultMap.put("data",resultList);
        return resultMap;
    }
}
