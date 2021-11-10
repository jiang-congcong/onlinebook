package com.cn.book.serviceimpl;

import com.cn.book.controller.SkuController;
import com.cn.book.dao.SkuDAO;
import com.cn.book.iservice.ISkuSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/10 18:52
 */
@Service
public class SkuSVImpl implements ISkuSV {

    public static Logger logger = LoggerFactory.getLogger(SkuSVImpl.class);

    @Autowired
    private SkuDAO skuDAO;

    @Override
    public void addBookSku(Map<String, Object> reqMap) throws Exception{
        if(null!=reqMap&&reqMap.size()>0){
            try{
                skuDAO.operationBookSku(reqMap);
            }catch (Exception e){
                logger.error("添加库存失败:"+e);
                throw new Exception("添加库存失败:"+e);
            }
        }
    }

    @Override
    public void cutBookStock(Map<String, Object> reqMap) throws Exception{
        String bookId = reqMap.get("bookId").toString();
        int stockCutNum = (Integer) reqMap.get("stockCutNum");
        Map<String,Object> querySkuInfo = skuDAO.queryBookSku(bookId);
        double skuTotal = (Double) querySkuInfo.get("skuTotal");
        double skuSale = (Double) querySkuInfo.get("skuSale");
        if(skuTotal-skuSale-stockCutNum<0){
            throw new Exception("减少库存大于剩余库存");
        }
        if(null!=reqMap&&reqMap.size()>0){
            try{
                skuDAO.operationBookSku(reqMap);
            }catch (Exception e){
                logger.error("扣减库存失败:"+e);
                throw new Exception("扣减库存失败:"+e);
            }
        }
    }

}
