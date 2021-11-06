package com.cn.book.serviceimpl;

import com.cn.book.controller.CartController;
import com.cn.book.dao.OrderDAO;
import com.cn.book.iservice.IOrderSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/5 19:37
 */
@Service
public class OrderSVImpl implements IOrderSV {

    public static Logger logger = LoggerFactory.getLogger(OrderSVImpl.class);

    @Autowired
    private OrderDAO orderDAO;

    @Override
    public boolean insertOrderInfo(List<Map<String,Object>> reqList) throws Exception{
        boolean result = true;
        try{
            orderDAO.insertOrderInfo(reqList);
        }catch (Exception e){
            logger.error("批量插入订单记录失败："+e);
            result = false;
        }
        return result;
    }

    @Override
    public Map<String,Object> queryOrderInfo(Map<String,Object> reqMap) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        try{
            List<Map<String,Object>> queryList = orderDAO.queryOrderInfo(reqMap);
            if(null!=queryList&&queryList.size()>0){
                resultMap = queryList.get(0);
                resultMap.put("describe",resultMap.get("introduce"));
                double d1 = (Double) resultMap.get("skuTotal");//原始库存总量
                double d2 = (Double) resultMap.get("skuSale");//销量
                resultMap.put("stockNum",d1-d2); //剩余库存
            }
        }catch (Exception e){
            logger.error("查询订单详情失败"+e);
        }
        return resultMap;
    }

    @Override
    public boolean updateOrderState(Map<String,Object> reqMap) throws Exception{
       boolean result = true;
       try{
           orderDAO.updateOrderState(reqMap);
       }catch (Exception e){
           logger.error("更新订单状态失败："+e);
           result = false;
       }
       return result;
    }

    @Override
    public Map<String,Object> queryUserOrderList(Map<String,Object> reqMap) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        int total = orderDAO.queryUserOrderListTotal(reqMap);
        resultMap.put("total",total);
        resultMap.put("data","");
        if(total>0){
            List<Map<String,Object>> resultList = orderDAO.queryUserOrderList(reqMap);
            resultMap.put("data",resultList);
        }
        return resultMap;
    }
}
