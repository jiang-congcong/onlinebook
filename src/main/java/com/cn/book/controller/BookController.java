package com.cn.book.controller;

import com.cn.book.iservice.IBookSV;
import com.cn.book.utils.Result;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/10/31 9:33
 */
@RestController
@RequestMapping("/book")
@Api(value = "/book",description = "书籍操作类")
public class BookController {

    public static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IBookSV iBookSV;

    @RequestMapping(method = RequestMethod.POST,value = "/queryList")
    @ResponseBody
    @ApiOperation(value = "查询图书列表")
    public Result queryList(@RequestBody Map<String,Object> reqMap) throws Exception{
        Result result = new Result();
        int page = 0;
        int size = 0;
        if(null==reqMap.get("page")||null==reqMap.get("size")){
            result.setRtnMessage("分页参数不能为空");
            result.setRtnCode("400");
        }
        else{
            page = (Integer) reqMap.get("page");
            size = (Integer) reqMap.get("size");
            int start = (Integer.valueOf(page)-1)*Integer.valueOf(size);
            reqMap.put("start",start);
            reqMap.put("limit",Integer.valueOf(size));
            reqMap.put("order","DESC");
            if(null!= reqMap.get("sort")){
                int sort = (Integer) reqMap.get("sort");
                if(sort>0){
                    reqMap.put("order","ASC");
                }
            }
            try {
                Map<String,Object> queryResultMap = iBookSV.queryBookList(reqMap);
                result.setRtnCode("200");
                result.setResult(queryResultMap);
            }catch (Exception e){
                logger.error("查询图书列表失败！"+e);
                result.setRtnCode("400");
                result.setRtnMessage("查询图书列表失败");
            }
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/home")
    @ResponseBody
    @ApiOperation(value = "主页查询")
    public Result mainHome() throws Exception{
        Result result = new Result();
        try{
            List<Map<String,Object>> resultList = iBookSV.queryHomePage();
            Map<String,Object> putMap = new HashMap<>();
            putMap.put("data",resultList);
            result.setRtnCode("200");
            result.setResult(putMap);
        }catch (Exception e){
            logger.error("查询主页商品失败！"+e);
        }
        return result;
    }

}
