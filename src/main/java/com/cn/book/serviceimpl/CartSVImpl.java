package com.cn.book.serviceimpl;

import com.cn.book.dao.CartDAO;
import com.cn.book.iservice.ICartSV;
import com.cn.book.utils.CommonUtils;
import com.xiaoleilu.hutool.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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

    @Autowired
    private CommonUtils commonUtils;

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
                        String image = (String)eachMap.get("image");
                        image = commonUtils.dealImageTobase64(image);
                        eachMap.put("image",image);
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

    public boolean addCart(Map<String,Object> reqMap) throws Exception{
        boolean result = false;
        if(null!=reqMap&&reqMap.size()>0){
            String cartId = (String)reqMap.get("cartId");
            if(StrUtil.hasEmpty(cartId)){ //新增购物车记录
                try {
                    String createCartId = commonUtils.createAllId();
                    reqMap.put("cartId",createCartId);
                    cartDAO.insertNewCart(reqMap);
                    result = true;
                }catch (Exception e){
                    logger.error("新增购物车失败");
                }
            }
            else{ //更新数量
                String cartBookNum = "";
                try {
                    cartBookNum = cartDAO.selectCartBookNum(cartId);
                }catch (Exception e){
                    logger.error("查询购物车数量失败！");
                }
                String operateTye = (String)reqMap.get("operateType");
                int modfNum = (Integer) reqMap.get("bookNum");
                if(!StrUtil.hasEmpty(cartBookNum)){
                    if("1".equals(operateTye)) {
                        modfNum = modfNum + Integer.valueOf(cartBookNum);
                    }
                    else if("0".equals(operateTye)){
                        modfNum = Integer.valueOf(cartBookNum) - modfNum;
                    }
                }
                reqMap.put("bookNum",modfNum);
                try {
                    cartDAO.updateCartBook(reqMap);
                    result = true;
                }catch (Exception e){
                    logger.error("更新购物车数量失败！");
                }
            }
        }
        return result;
    }

    public boolean deleteCart(@RequestBody Map<String,Object> reqMap) throws Exception{
        boolean flag = false;
        try{
            cartDAO.deleteCartBook(reqMap);
            flag = true;
        }catch (Exception e){
            logger.error("删除购物车失败！");
        }
        return flag;
    }
}
