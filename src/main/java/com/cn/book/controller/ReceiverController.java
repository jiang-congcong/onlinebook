package com.cn.book.controller;

import com.cn.book.iservice.IReceiverSV;
import com.cn.book.utils.CommonUtils;
import com.cn.book.utils.Result;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/4 20:06
 */
@RestController
@RequestMapping("/receiver")
@Api(value = "/receiver",description = "收货人信息操作类")
public class ReceiverController {

    @Autowired
    private IReceiverSV iReceiverSV;

    @Autowired
    private CommonUtils commonUtils;

    public static Logger logger = LoggerFactory.getLogger(ReceiverController.class);

    @RequestMapping(method = RequestMethod.POST,value = "/receiverList")
    @ResponseBody
    @ApiOperation(value = "查询收货人信息列表")
    public Result queryReceiverList(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        if(StrUtil.hasEmpty(userId)){
            result.setRtnCode("400");
            result.setRtnMessage("用户id不能为空");
            return result;
        }
        try {
            List<Map<String, Object>> resultList = iReceiverSV.queryReceiverList(reqMap);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", resultList);
            result.setResult(resultMap);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("查询收货人列表失败！");
            result.setRtnCode("400");
            result.setRtnMessage("用户id不能为空");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/addReceiver")
    @ResponseBody
    @ApiOperation(value = "添加收货人信息")
    public Result addReceiver(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        String receiverName = (String)reqMap.get("receiverName");
        String receiverPhone = (String)reqMap.get("receiverPhone");
        String receiverAddress = (String)reqMap.get("receiverAddress");

        if(StrUtil.hasEmpty(userId)||StrUtil.hasEmpty(receiverName)||StrUtil.hasEmpty(receiverPhone)||StrUtil.hasEmpty(receiverAddress)||null==reqMap.get("isDefault")){
            result.setRtnCode("400");
            result.setRtnMessage("用户id、收货人姓名、手机号、地址、是否是默认地址均不能为空");
            return result;
        }
        boolean isDefault = (Boolean)reqMap.get("isDefault");
        reqMap.put("isDefault","0");
        if(isDefault){
            reqMap.put("isDefault","1");
        }
        String receiverId = commonUtils.createAllId();
        reqMap.put("receiverId",receiverId);
        boolean flag = iReceiverSV.addReceiver(reqMap);
        if(flag){
            result.setRtnCode("200");
        }
        else{
            result.setRtnCode("400");
            result.setRtnMessage("新增收货人失败！");
        }
        return result;
    }

}
