package com.cn.book.controller;

import com.cn.book.iservice.IOrderSV;
import com.cn.book.utils.CommonUtils;
import com.cn.book.utils.Result;
import com.google.common.util.concurrent.RateLimiter;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jiangcongcong
 * @date 2021/11/5 19:36
 */
@RestController
@RequestMapping("/0rder")
@Api(value = "/order",description = "订单操作类")
public class OrderController {

    public static Logger logger = LoggerFactory.getLogger(OrderController.class);

    private RateLimiter rateLimiter = RateLimiter.create(500);//令牌桶内一次创建500哥令牌

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private IOrderSV iOrderSV;

    @RequestMapping(method = RequestMethod.POST,value = "/submit")
    @ResponseBody
    @ApiOperation(value = "用户下单")
    public Result insertOrderInfo(@RequestBody Map<String,Object> reqMap) throws Exception{
        Result result = new Result();
        if(!rateLimiter.tryAcquire(2, TimeUnit.SECONDS)){
            result.setRtnMessage("下单失败，活动过于火爆，请重试");
            result.setRtnCode("-400");
            return result;
        }
        String userId = (String)reqMap.get("userId");
        String receiverId = (String)reqMap.get("receiverId");
        String receiverName = (String)reqMap.get("receiverName");
        String receiverPhone = (String)reqMap.get("receiverPhone");
        String receiverAddress = (String)reqMap.get("receiverAddress");
        if(StrUtil.hasEmpty(userId)||StrUtil.hasEmpty(receiverId)||StrUtil.hasEmpty(receiverName)||StrUtil.hasEmpty(receiverPhone)||StrUtil.hasEmpty(receiverAddress)){
            result.setRtnCode("400");
            result.setRtnMessage("用户id、收货人id、收货人姓名、电话、手机号均不能为空");
            return result;
        }

        List<Map<String,Object>> bookList = (List<Map<String,Object>>)reqMap.get("bookList");
        if(null!=bookList&&bookList.size()>0){
            for(Map<String,Object> eachMap:bookList){
                String orderId = commonUtils.createAllId();//生成订单号
                eachMap.put("orderId",orderId);
                eachMap.put("userId",userId);
                eachMap.put("receiverId",receiverId);
                eachMap.put("receiverName",receiverName);
                eachMap.put("receiverPhone",receiverPhone);
                eachMap.put("receiverAddress",receiverAddress);
                eachMap.put("validState","0");
            }
        }
        try{
            iOrderSV.insertOrderInfo(bookList);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("用户下单失败："+e);
            result.setRtnCode("400");
            result.setRtnMessage("用户下单失败");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/detail")
    @ResponseBody
    @ApiOperation(value = "查询订单详情")
    public Result queryOrderInfo(@RequestBody Map<String,Object> reqMap) throws Exception{
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        String orderId = (String)reqMap.get("orderId");
        if(StrUtil.hasEmpty(userId)||StrUtil.hasEmpty(orderId)){
            result.setRtnCode("400");
            result.setRtnMessage("用户id和订单id均不能为空");
        }
        try{
            Map<String,Object> resultMap = iOrderSV.queryOrderInfo(reqMap);
            result.setRtnCode("200");
            result.setResult(resultMap);
        }catch (Exception e){
            logger.error("查询订单详情失败:"+e);
            result.setRtnCode("400");
            result.setRtnMessage("查询订单详情失败");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/payment")
    @ResponseBody
    @ApiOperation(value = "订单状态更新")
    public Result updateOrderState(@RequestBody Map<String,Object> reqMap) throws Exception{
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        String orderId = (String)reqMap.get("orderId");
        String orderState = (String)reqMap.get("orderState");
        if(StrUtil.hasEmpty(userId)||StrUtil.hasEmpty(orderId)||StrUtil.hasEmpty(orderState)){
            result.setRtnCode("400");
            result.setRtnMessage("用户id、订单id、订单状态均不能为空");
        }
        try{
            iOrderSV.updateOrderState(reqMap);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("更新状态失败"+e);
            result.setRtnCode("400");
            result.setRtnMessage("更新状态失败");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/delOrder")
    @ResponseBody
    @ApiOperation(value = "删除订单")
    public Result deleteOrder(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        String orderId = (String)reqMap.get("orderId");
        if(StrUtil.hasEmpty(userId)||StrUtil.hasEmpty(orderId)){
            result.setRtnCode("400");
            result.setRtnMessage("用户id、订单id均不能为空");
        }
        reqMap.put("orderState","3");
        try{
            iOrderSV.updateOrderState(reqMap);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("删除订单失败"+e);
            result.setRtnCode("400");
            result.setRtnMessage("删除订单失败");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/list")
    @ResponseBody
    @ApiOperation(value = "查询订单列表")
    public Result queryUserOrderList(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        int page = (Integer) reqMap.get("page");
        int size = (Integer) reqMap.get("size");
        if(StrUtil.hasEmpty(userId)){
            result.setRtnCode("400");
            result.setRtnMessage("用户id不能为空");
            return result;
        }
        int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
        reqMap.put("start", start);
        reqMap.put("limit", size);
        try{
            Map<String,Object> resultMap = iOrderSV.queryUserOrderList(reqMap);
            result.setResult(resultMap);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("查询订单列表失败"+e);
            result.setRtnCode("400");
            result.setRtnMessage("查询订单列表失败");
        }
        return result;
    }

}
