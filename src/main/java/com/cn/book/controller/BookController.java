package com.cn.book.controller;

import com.cn.book.config.EsRestClient;
import com.cn.book.iservice.IBookSV;
import com.cn.book.utils.CommonUtils;
import com.cn.book.utils.Result;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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

    @Autowired
    private EsRestClient esRestClient;

    @Autowired
    private CommonUtils commonUtils;

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

    @RequestMapping(method = RequestMethod.POST,value = "/detail")
    @ResponseBody
    @ApiOperation(value = "查询书籍详情")
    public Result queryBookDetail(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        String bookId = (String)reqMap.get("bookId");
        if(StrUtil.hasEmpty(bookId)){
            result.setRtnMessage("书籍ID不能为空！");
            result.setRtnCode("400");
            return result;
        }
        //先查ES，ES不存在再查数据库
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(100);
        searchSourceBuilder.sort("age");
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("id.keyword", bookId);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQueryBuilder);
        searchSourceBuilder.query(boolQueryBuilder);
        Map<String, Object> resultMap = esRestClient.queryMessageFromES("book", searchSourceBuilder);
        List<Map<String,Object>> resultList = (List<Map<String,Object>>)resultMap.get("resultList");
        for(Map<String,Object>eachMap:resultList){
            String image = (String)eachMap.get("image");
            if(!StrUtil.hasEmpty(image)) {
                String base64Str = commonUtils.dealImageTobase64(image);
                eachMap.put("image",base64Str);
            }
            String detail = (String)eachMap.get("detail");
            if(!StrUtil.hasEmpty(detail)) {
                String base64Str = commonUtils.dealImageTobase64(detail);
                eachMap.put("detail",base64Str);
            }
            List<String> smallList = (List<String>)eachMap.get("imageSmall");
            List<String> base64StrList = new ArrayList<>();
            if(null!=smallList&&smallList.size()>0){
                for(String eachStr:smallList){
                    String smallStr = commonUtils.dealImageTobase64(eachStr);
                    base64StrList.add(smallStr);
                }
                reqMap.put("imageSmall",base64StrList);
            }
        }
        if(null!=resultList&&resultList.size()>0){
            Map<String,Object> putMap = new HashMap<>();
            putMap.put("data",resultList);
            result.setResult(putMap);
        }
        else {
            Map<String, Object> queryDetailMap = iBookSV.queryBookDetail(bookId);
//            result.setResult(queryDetailMap);
            //将详情存入ES
            String index = "book";
            String type = "_doc";
            Map map = new HashMap();
            map.put("data", queryDetailMap);
            map.put("id",bookId);
            try {
                esRestClient.putMessageToES(index, type, map, bookId);
            } catch (Exception e) {
                logger.error("插入ES失败" + e.getMessage());
                throw new Exception("插入ES失败"+e);
            }

            String image = (String)queryDetailMap.get("image");
            if(!StrUtil.hasEmpty(image)) {
                String base64Str = commonUtils.dealImageTobase64(image);
                queryDetailMap.put("image",base64Str);
            }
            String detail = (String)queryDetailMap.get("detail");
            if(!StrUtil.hasEmpty(detail)) {
                String base64Str = commonUtils.dealImageTobase64(detail);
                queryDetailMap.put("detail",base64Str);
            }
            List<String> smallList = (List<String>)queryDetailMap.get("imageSmall");
            List<String> base64StrList = new ArrayList<>();
            if(null!=smallList&&smallList.size()>0){
                for(String eachStr:smallList){
                    String smallStr = commonUtils.dealImageTobase64(eachStr);
                    base64StrList.add(smallStr);
                }
                queryDetailMap.put("imageSmall",base64StrList);
            }

            result.setResult(queryDetailMap);
        }
        result.setRtnCode("200");
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/addBook")
    @ResponseBody
    @ApiOperation(value = "添加书籍")
    public Result addBook(@RequestBody Map<String,Object> reqMap) throws Exception {
        /**
         * {
         * "userId":"20211030112600341004",
         * "shopId":"1",
         * "name":"三国演义",
         * "image":"localhost:8080/pic/img03.jpg",
         * "price":100,
         * "describe":"简爱描述",
         * "type":"2",
         * "imageSmall":[
         * "localhost:8080/pic/img04.jpg",
         * "localhost:8080/pic/img05.jpg"
         * ],
         * "detail":"localhost:8080/pic/img06.jpg",
         * "skuNum":100
         *
         * }**/

        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        String shopId = (String)reqMap.get("shopId");
        String name = (String)reqMap.get("name");
        String type = (String)reqMap.get("type");
        String image = (String)reqMap.get("image");
        String detail = (String)reqMap.get("detail");
        if(StrUtil.hasEmpty(shopId)||StrUtil.hasEmpty(userId) ||StrUtil.hasEmpty(name)||null==reqMap.get("price")||StrUtil.hasEmpty(type)||StrUtil.hasEmpty(image)||StrUtil.hasEmpty(detail)){
            result.setRtnCode("400");
            result.setRtnMessage("商户id、用户id、书名、价格、类型、图片、详情图片均不能为空");
            return result;
        }
        //base64转图片地址
        String imagePath = commonUtils.dealbase64ToImagePath(image);
        reqMap.put("image",imagePath);
        String detailPath = commonUtils.dealbase64ToImagePath(detail);
        reqMap.put("detail",detailPath);
        List<String> imageSmall = (List<String>) reqMap.get("imageSmall");
        List<String> imageSmallPath = new ArrayList<>();
        if(null!=imageSmall&&imageSmall.size()>0){
            for(String eachStr:imageSmall){
                String smallPath = commonUtils.dealbase64ToImagePath(eachStr);
                imageSmallPath.add(smallPath);
            }
            reqMap.put("imageSmall",imageSmallPath);
        }
        try{
            iBookSV.addBook(reqMap);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("保存书籍失败："+e);
            result.setRtnCode("400");
            result.setRtnMessage("保存书籍失败");
        }

        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/delBook")
    @ResponseBody
    @ApiOperation(value = "删除书籍")
    public Result delBook(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        String bookId = (String)reqMap.get("bookId");
        String userId = (String)reqMap.get("userId");
        if(StrUtil.hasEmpty(bookId)||StrUtil.hasEmpty(userId)){
            result.setRtnCode("400");
            result.setRtnMessage("书籍ID、用户ID均不能为空！");
            return result;
        }
        try{
            List<String> bookIdList = new ArrayList<>();
            bookIdList.add(bookId);
            reqMap.put("bookIdList",bookIdList);
            reqMap.put("validState","2");
            iBookSV.operateBookValidState(reqMap);
            result.setRtnCode("200");
            //删除ES书籍数据
            for(String eachBookId:bookIdList) {
                esRestClient.deleteMessageFromES("book", "_doc", eachBookId);
            }
        }catch (Exception e){
            logger.error("删除书籍失败:"+e);
            result.setRtnCode("400");
            result.setRtnMessage("删除书籍失败");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/offShelf")
    @ResponseBody
    @ApiOperation(value = "书籍下架")
    public Result offShelf(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        List<String> booksId = (List<String>)reqMap.get("booksId");
        String userId = (String)reqMap.get("userId");
        if(null==booksId||StrUtil.hasEmpty(userId)){
            result.setRtnCode("400");
            result.setRtnMessage("书籍ID、用户ID均不能为空！");
            return result;
        }
        try{
            reqMap.put("validState","0");
            reqMap.put("bookIdList",booksId);
            iBookSV.operateBookValidState(reqMap);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("下架书籍失败:"+e);
            result.setRtnCode("400");
            result.setRtnMessage("下架书籍失败");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/onShelf")
    @ResponseBody
    @ApiOperation(value = "书籍上架")
    public Result onShelf(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        List<String> booksId = (List<String>)reqMap.get("booksId");
        String userId = (String)reqMap.get("userId");
        if(null==booksId||StrUtil.hasEmpty(userId)){
            result.setRtnCode("400");
            result.setRtnMessage("书籍ID、用户ID均不能为空！");
            return result;
        }
        try{
            reqMap.put("validState","1");
            reqMap.put("bookIdList",booksId);
            iBookSV.operateBookValidState(reqMap);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("上架书籍失败:"+e);
            result.setRtnCode("400");
            result.setRtnMessage("上架书籍失败");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/updateBook")
    @ResponseBody
    @ApiOperation(value = "书籍修改")
    public Result updateBook(@RequestBody Map<String,Object> reqMap) throws Exception {
        /**
         * {
         * "userId":"20211030112600341004",
         * "bookId":"20211109204300161032",
         * "name":"红楼",
         * "image":"05.jpg",
         * "price":200,
         * "describe":"红楼梦",
         * "detail":"06.jpg",
         * "imageSmall":[
         * "07.jpg",
         * "08.jpg"
         * ]
         * }**/

        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        String name = (String)reqMap.get("name");
        String image = (String)reqMap.get("image");
        String detail = (String)reqMap.get("detail");
        if(StrUtil.hasEmpty(userId) ||StrUtil.hasEmpty(name)||null==reqMap.get("price")||StrUtil.hasEmpty(image)||StrUtil.hasEmpty(detail)){
            result.setRtnCode("400");
            result.setRtnMessage("商户id、用户id、书名、价格、类型、图片、详情图片均不能为空");
            return result;
        }
        //base64转图片地址
        String imagePath = commonUtils.dealbase64ToImagePath(image);
        reqMap.put("image",imagePath);
        String detailPath = commonUtils.dealbase64ToImagePath(detail);
        reqMap.put("detail",detailPath);
        List<String> imageSmall = (List<String>) reqMap.get("imageSmall");
        List<String> imageSmallPath = new ArrayList<>();
        if(null!=imageSmall&&imageSmall.size()>0){
            for(String eachStr:imageSmall){
                String smallPath = commonUtils.dealbase64ToImagePath(eachStr);
                imageSmallPath.add(smallPath);
            }
            reqMap.put("imageSmall",imageSmallPath);
        }
        try{
            iBookSV.updateBook(reqMap);
            result.setRtnCode("200");
            //同步修改ES书籍信息
            String bookId = reqMap.get("bookId").toString();
            Map<String, Object> queryDetailMap = iBookSV.queryBookDetail(bookId); //先查详情，再存ES
            result.setResult(queryDetailMap);
            //将详情存入ES
            String index = "book";
            String type = "_doc";
            Map map = new HashMap();
            map.put("data", queryDetailMap);
            map.put("id", bookId);
            try {
                esRestClient.putMessageToES(index, type, map, bookId);
            } catch (Exception e) {
                logger.error("插入ES失败" + e.getMessage());
                throw new Exception("插入ES失败"+e);
            }
        }catch (Exception e){
            logger.error("更新书籍信息失败"+e);
            result.setRtnCode("400");
            result.setRtnMessage("更新书籍信息失败");
        }
        return result;
    }



}
