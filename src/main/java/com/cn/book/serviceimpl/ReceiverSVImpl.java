package com.cn.book.serviceimpl;

import com.cn.book.dao.ReceiverDAO;
import com.cn.book.iservice.IReceiverSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/4 20:07
 */
@Service
public class ReceiverSVImpl implements IReceiverSV {

    @Autowired
    private ReceiverDAO receiverDAO;

    public static Logger logger = LoggerFactory.getLogger(ReceiverSVImpl.class);

    @Override
    public List<Map<String,Object>> queryReceiverList(Map<String,Object> reqMap) throws Exception{
        List<Map<String, Object>> resultList;
        try {
            resultList = receiverDAO.queryReceiverList(reqMap);
            if(null == resultList){
                resultList = new ArrayList<>();
            }
        }catch (Exception e){
            logger.error("查询用户列表失败："+e);
            resultList = new ArrayList<>();
        }
        return resultList;
    }

    @Override
    public boolean addReceiver(Map<String,Object> reqMap) throws Exception{
        boolean result = false;
        try{
            receiverDAO.addReceiverInfo(reqMap);
            result = true;
        }catch (Exception e){
            logger.error("新增收货地址失败");
        }
        return result;
    }

    @Override
    public boolean delReceiverInfo(Map<String,Object> reqMap) throws Exception{
        boolean result = true;
        try{
            receiverDAO.delReceiverInfo(reqMap);
        }catch (Exception e){
            logger.error("删除用户信息失败！"+e);
            result = false;
        }
        return result;
    }

    @Override
    public boolean updateReceiverInfo(Map<String,Object> reqMap) throws Exception{
        boolean result = true;
        try{
            receiverDAO.updateReceiverInfo(reqMap);
        }catch (Exception e){
            logger.error("更新用户信息失败！"+e);
            result = false;
        }
        return result;
    }
}
