package com.cn.book.controller;

import com.cn.book.iservice.ICartSV;
import com.cn.book.utils.Result;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/3 20:31
 */

@RestController
@RequestMapping("/cart")
public class CartController {

    public static Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private ICartSV iCartSV;

    @RequestMapping(method = RequestMethod.POST,value = "/cartList")
    @ResponseBody
    @ApiOperation(value = "查询购物车列表")
    public Result queryList(@RequestBody Map<String,Object> reqMap) throws Exception{
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        if(StrUtil.hasEmpty(userId)){
            result.setRtnMessage("用户id不能为空");
            result.setRtnCode("400");
            return result;
        }
        int page = 0;
        int size = 0;
        if(null==reqMap.get("page")||null==reqMap.get("size")){
            result.setRtnMessage("分页参数不能为空");
            result.setRtnCode("400");
        }
        else {
            page = (Integer) reqMap.get("page");
            size = (Integer) reqMap.get("size");
            int start = (Integer.valueOf(page) - 1) * Integer.valueOf(size);
            reqMap.put("start", start);
            reqMap.put("limit", Integer.valueOf(size));
            Map<String,Object> queryDataMap = iCartSV.queryCartListByUserId(reqMap);
        }
        return result;
    }

}
