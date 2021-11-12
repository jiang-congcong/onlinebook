package com.cn.book.serviceimpl;

import com.cn.book.controller.CartController;
import com.cn.book.dao.OrderDAO;
import com.cn.book.iservice.IOrderSV;
import com.cn.book.utils.CommonUtils;
import io.swagger.models.auth.In;
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
 * @date 2021/11/5 19:37
 */
@Service
public class OrderSVImpl implements IOrderSV {

    public static Logger logger = LoggerFactory.getLogger(OrderSVImpl.class);

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private CommonUtils commonUtils;

    @Override
    public boolean insertOrderInfo(List<Map<String,Object>> reqList) throws Exception{
        boolean result = true;
        try{
            //orderDAO.insertOrderInfo(reqList);
            List<String> bookIdList = new ArrayList<>();
            for(Map<String,Object> eachMap:reqList){
                String bookId = eachMap.get("bookId").toString();
                bookIdList.add(bookId);
            }
            List<Map<String,Object>> querySkuInfo = orderDAO.selectBookSku(bookIdList);
            Map<String,Map<String,Object>> dealDataMap = new HashMap<>();
            for(Map<String,Object> eachMap:querySkuInfo){
                dealDataMap.put(eachMap.get("bookId").toString(),eachMap);
            }
            for(Map<String,Object> eachMap:reqList){
                int bookNum = (Integer) eachMap.get("bookNum");//下单数量
                String bookId = eachMap.get("bookId").toString();
                Map<String,Object> getMap = dealDataMap.get(bookId);
                double skuTotal = (Double) getMap.get("skuTotal");
                double skuSale = (Double) getMap.get("skuSale");
                if(bookNum+skuSale>skuTotal){
                    logger.error("书籍id"+bookId+":下单数量大于库存数量！");
                    throw new Exception("书籍id"+bookId+":下单数量大于库存数量！");
                }
                getMap.put("saleNum",bookNum);
                try {
                    orderDAO.updateBookSku(getMap);
                }catch (Exception e){
                    logger.error("更新库存失败");
                    throw new Exception("更新库存失败！");
                }
            }
            orderDAO.insertOrderInfo(reqList);//写入订单

            Map<String,Object> deleteCartInfo = new HashMap<>();//删除购物车
            List<String> cartIdList = new ArrayList<>();
            for(Map<String,Object> eachMap:reqList){
                if(null!=eachMap.get("cartId")){
                    cartIdList.add(eachMap.get("cartId").toString());
                }
            }
            deleteCartInfo.put("cartIdList",cartIdList);
            deleteCartInfo.put("userId",reqList.get(0).get("userId"));


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
                String image = (String)resultMap.get("image");
                image = commonUtils.dealImageTobase64(image);
                resultMap.put("image",image);
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
            if(null!=resultList&&resultList.size()>0){
                for(Map<String,Object> eachMap:resultList){
                    String image = (String)eachMap.get("image");
                    image = commonUtils.dealImageTobase64(image);
                    eachMap.put("image",image);
                }
            }
            resultMap.put("data",resultList);
        }
        return resultMap;
    }

    @Override
    public void updateOrderInfo(Map<String,Object> reqMap) throws Exception{
        if(null!=reqMap&&reqMap.size()>0){
            try{
                orderDAO.updateOrderInfo(reqMap);
            }catch (Exception e){
                logger.error("更新订单失败："+e);
                throw new Exception("更新订单失败："+e);
            }
        }
    }
}
