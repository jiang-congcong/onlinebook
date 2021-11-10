package com.cn.book.controller;

import com.cn.book.iservice.ISkuSV;
import com.cn.book.utils.Result;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/11/10 18:47
 */
@RestController
@RequestMapping("/stock")
@Api(value = "/stock",description = "库存操作类")
public class SkuController {

    public static Logger logger = LoggerFactory.getLogger(SkuController.class);

    @Autowired
    private ISkuSV iSkuSV;

    @RequestMapping(method = RequestMethod.POST,value = "/addStock")
    @ResponseBody
    @ApiOperation(value = "添加库存")
    public Result addStock(@RequestBody Map<String,Object> reqMap){
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        String bookId = (String)reqMap.get("bookId");
        int name = (Integer) reqMap.get("stockAddNum");
        if(StrUtil.hasEmpty(bookId)||StrUtil.hasEmpty(userId) || name<0){
            result.setRtnCode("400");
            result.setRtnMessage("用户ID、书籍ID不能为空且添加库存数量大于零");
            return result;
        }
        try{
            iSkuSV.addBookSku(reqMap);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("添加库存失败："+e);
            result.setRtnCode("400");
            result.setRtnMessage("添加库存失败");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/cutStock")
    @ResponseBody
    @ApiOperation(value = "扣减库存")
    public Result cutStock(@RequestBody Map<String,Object> reqMap){
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        String bookId = (String)reqMap.get("bookId");
        int name = (Integer) reqMap.get("stockCutNum");
        if(StrUtil.hasEmpty(bookId)||StrUtil.hasEmpty(userId) || name<0){
            result.setRtnCode("400");
            result.setRtnMessage("用户ID、书籍ID不能为空且扣减库存数量大于零");
            return result;
        }
        try{
            iSkuSV.cutBookStock(reqMap);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("减少库存失败："+e);
            result.setRtnCode("400");
            result.setRtnMessage("减少库存失败");
        }
        return result;
    }



}
