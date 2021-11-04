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
            try {
                Map<String, Object> queryDataMap = iCartSV.queryCartListByUserId(reqMap);
                result.setRtnCode("200");
                result.setResult(queryDataMap);
            }catch (Exception e){
                logger.error("查询购物车列表异常："+e);
                result.setRtnMessage("查询购物车列表异常");
                result.setRtnCode("400");
            }

        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/addCart")
    @ResponseBody
    @ApiOperation(value = "添加购物车")
    public Result addCart(@RequestBody Map<String,Object> reqMap) throws Exception{
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        String bookId = (String)reqMap.get("bookId");
        //int bookNum = (Integer)reqMap.get("bookNum");
        String operateType = (String)reqMap.get("operateType");
        if(StrUtil.hasEmpty(userId)||StrUtil.hasEmpty(bookId)||null==reqMap.get("bookNum")||StrUtil.hasEmpty(operateType)){
            result.setRtnCode("400");
            result.setRtnMessage("用户id或书籍id或书籍数量或操作类型不能为空！");
            return result;
        }

        boolean flag = iCartSV.addCart(reqMap);
        if(flag){
            result.setRtnCode("200");
            result.setRtnMessage("添加购物车成功");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/delCart")
    @ResponseBody
    @ApiOperation(value = "删除购物车")
    public Result deleteCart(@RequestBody Map<String,Object> reqMap) throws Exception{
        Result result = new Result();
        String cartId = (String)reqMap.get("cartId");
        String userId = (String)reqMap.get("userId");
        if(StrUtil.hasEmpty(cartId)||StrUtil.hasEmpty(userId)){
            result.setRtnCode("400");
            result.setRtnMessage("购物车id或用户id不能为空");
            return result;
        }

        boolean flag = iCartSV.deleteCart(reqMap);
        if(flag){
            result.setRtnCode("200");
            result.setRtnMessage("删除购物车成功！");
        }
        else{
            result.setRtnCode("400");
            result.setRtnMessage("删除购物车失败！");
        }
        return result;
    }

}
