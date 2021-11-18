package com.cn.book.serviceimpl;

import com.cn.book.controller.UserController;
import com.cn.book.dao.BookDAO;
import com.cn.book.dao.OrderDAO;
import com.cn.book.iservice.IBookSV;
import com.cn.book.utils.CommonUtils;
import com.xiaoleilu.hutool.util.StrUtil;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/10/31 9:45
 */
@Service
public class BookSVImpl implements IBookSV {

    public static Logger logger = LoggerFactory.getLogger(BookSVImpl.class);

    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private OrderDAO orderDAO;

    @Override
    public Map<String,Object> queryBookList(Map<String,Object> reqMap) throws Exception{
        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();
        int total = 0;
        if(null!=reqMap&&reqMap.size()>0){
            total = bookDAO.queryBookListTotal(reqMap);
            if(total>0) {
                List<Map<String, Object>> midList = bookDAO.queryBookList(reqMap);
                if (null != midList && midList.size() > 0) {
                    for(Map<String,Object> eachMap:midList){
                        String validState = eachMap.get("validState").toString();
                        if("1".equals(validState)){
                            eachMap.put("isSale",true);
                        }
                        else{
                            eachMap.put("isSale",false);
                        }
                        String bookId = eachMap.get("bookId").toString();
                        List<String> bookImageSmall = bookDAO.queryBookImageSmall(bookId);
                        List<String> bookBigImg = bookDAO.queryBookBigPic(bookId);
                        if(null!=bookImageSmall&&bookImageSmall.size()>0) {
                            List<String> resultListStr = new ArrayList<>();
                            for(String eachStr:bookImageSmall) {
                                String base64Str = commonUtils.dealImageTobase64(eachStr);
                                resultListStr.add(base64Str);
                            }
                            eachMap.put("imageSmall",resultListStr);
                        }
                        if(null!=bookBigImg&&bookBigImg.size()>0) {
                            String base64Str = commonUtils.dealImageTobase64(bookBigImg.get(0));
                            eachMap.put("detail",base64Str);
                        }
                        eachMap.put("describe",eachMap.get("introduce"));
                        double d1 = (Double) eachMap.get("stockTotal");//原始库存总量
                        double d2 = (Double) eachMap.get("stockSale");//销量
                        eachMap.put("stockNum",d1-d2); //剩余库存
                        //图片转base64
                        String image = (String)eachMap.get("image");
                        if(!StrUtil.hasEmpty(image)) {
                            String base64Str = commonUtils.dealImageTobase64(image);
                            eachMap.put("image",base64Str);
                        }
                    }
                    resultList = midList;
                }
            }
        }
        resultMap.put("total",total);
        resultMap.put("bookList",resultList);
        return resultMap;
    }

    @Override
     public List<Map<String,Object>> queryHomePage() throws Exception{
        List<Map<String,Object>> resultList = new ArrayList<>();
        List<Map<String,Object>> allCatgId = bookDAO.queryAllCatgId();
        if(null!=allCatgId&&allCatgId.size()>0){
            for(Map<String,Object> eachMap:allCatgId) {
                String catgId = (String)eachMap.get("catgId");
                Map<String, Object> reqMap = new HashMap<>();
                reqMap.put("start", 0);
                reqMap.put("limit", 4);
                reqMap.put("catgId", catgId);
                reqMap.put("validState","1");
                List<Map<String, Object>> queryBookList = bookDAO.queryBookList(reqMap);
                if(null!=queryBookList&&queryBookList.size()>0){
                    for(Map<String,Object> eachInfoMap:queryBookList){
                        //图片转base64
                        String image = (String)eachInfoMap.get("image");
                        if(!StrUtil.hasEmpty(image)) {
                            String base64Str = commonUtils.dealImageTobase64(image);
                            eachInfoMap.put("image",base64Str);
                        }
                    }
                    Map<String,Object> eachResultMap = new HashMap<>();
                    eachResultMap.put("type",eachMap.get("catgName"));
                    eachResultMap.put("data",queryBookList);
                    resultList.add(eachResultMap);
                }
            }
        }
        return resultList;
    }

    @Override
    public Map<String,Object> queryBookDetail(String bookId) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put("bookId",bookId);
        reqMap.put("start", 0);
        reqMap.put("limit", 10);
        reqMap.put("validState","1");
        List<Map<String, Object>> queryBookList = new ArrayList<>();
        try {
            queryBookList = bookDAO.queryBookList(reqMap);
        }catch (Exception e){
            logger.error("查询图书详情失败");
        }
        if(null!=queryBookList&&queryBookList.size()>0){
            resultMap = queryBookList.get(0);
            resultMap.put("describe",resultMap.get("introduce"));
//            String image = (String)resultMap.get("image");
//            if(!StrUtil.hasEmpty(image)) {
//                String base64Str = commonUtils.dealImageTobase64(image);
//                resultMap.put("image",base64Str);
//            }
            List<String> bookImageSmall = new ArrayList<>();
            List<String> bookBigImg = new ArrayList<>();
            try {
                bookImageSmall = bookDAO.queryBookImageSmall(bookId);
                bookBigImg = bookDAO.queryBookBigPic(bookId);
                if(null!=bookImageSmall&&bookImageSmall.size()>0){
                    resultMap.put("imageSmall",bookImageSmall);
                }
                if(null!=bookBigImg&&bookBigImg.size()>0){
                    resultMap.put("detail",bookBigImg.get(0));
                }
            }catch (Exception e){
                logger.error("查询图书图片组合或大图失败");
            }
//            resultMap.put("imageSmall",bookImageSmall);
//            if(null!=bookBigImg&&bookBigImg.size()>0){
//                resultMap.put("detail",bookBigImg.get(0));
//            }
//            else{
//                resultMap.put("detail","");
//            }
        }
        return resultMap;
    }

    @Override
    public boolean addBook(@RequestBody Map<String,Object> reqMap) throws Exception{
        boolean result = true;
        try {
            String bookId = commonUtils.createAllId();//创建书籍id
            String skuId = commonUtils.createAllId();//库存id
            Map<String, Object> insertBookMap = new HashMap<>();
            insertBookMap.put("bookId", bookId);
            insertBookMap.put("bookName", reqMap.get("name"));
            insertBookMap.put("bookPrice", reqMap.get("price"));
            insertBookMap.put("catgId", reqMap.get("catgId"));
            insertBookMap.put("describe", reqMap.get("describe"));
            insertBookMap.put("shopId", reqMap.get("shopId"));
            insertBookMap.put("shopName", reqMap.get("shopName"));
            insertBookMap.put("skuId", skuId);
            insertBookMap.put("validState", "0");
            insertBookMap.put("userId", reqMap.get("userId"));
            bookDAO.addBook(insertBookMap);
            insertBookMap.put("validState", "1");
            bookDAO.insertBookShopRL(insertBookMap);//书籍与商户关系
            bookDAO.insertBookCatgRL(insertBookMap);//书籍与类目关系
            Map<String, Object> insertBookSku = new HashMap<>();
            insertBookSku.put("skuId", skuId);
            insertBookSku.put("skuTotal", 0);
            insertBookSku.put("skuSale", 0);
            insertBookSku.put("validState", "1");
            insertBookSku.put("userId", reqMap.get("userId"));
            insertBookSku.put("version", 1);
            bookDAO.insertBookSku(insertBookSku);
            Map<String, Object> insertSkuRL = new HashMap<>();
            insertSkuRL.put("bookId", bookId);
            insertSkuRL.put("skuId", skuId);
            insertSkuRL.put("validState", "1");
            insertSkuRL.put("userId", reqMap.get("userId"));
            bookDAO.insertBookSkuRl(insertSkuRL);
            Map<String, Object> insertPic = new HashMap<>();
            String pictureId = commonUtils.createAllId();
            insertPic.put("bookId", bookId);
            insertPic.put("pictureId", pictureId);
            insertPic.put("pictureUrl", reqMap.get("image"));
            insertPic.put("validState", "1");
            insertPic.put("userId", reqMap.get("userId"));
            bookDAO.insertBookPicture(insertPic);

            String bigBookPic = commonUtils.createAllId();
            insertPic.put("pictureId", bigBookPic);
            insertPic.put("pictureUrl", reqMap.get("detail"));
            bookDAO.insertBigBookPicture(insertPic);

            List<String> imageSmall = (List<String>) reqMap.get("imageSmall");
            if (null != imageSmall && imageSmall.size() > 0) {
                for (String eachImageSmall : imageSmall) {
                    Map<String, Object> eachInsertPic = new HashMap<>();
                    String eachPictureId = commonUtils.createAllId();
                    eachInsertPic.put("bookId", bookId);
                    eachInsertPic.put("pictureId", eachPictureId);
                    eachInsertPic.put("pictureUrl", eachImageSmall);
                    eachInsertPic.put("validState", "1");
                    eachInsertPic.put("userId", reqMap.get("userId"));
                    bookDAO.insertBookSmall(eachInsertPic);
                }
            }
        }catch (Exception e){
            logger.error("保存书籍失败："+e);
            throw new Exception("保存书籍失败："+e);
        }
        return result;
    }

    @Override
    public void operateBookValidState(@RequestBody Map<String,Object> reqMap) throws Exception{
        try {
            List<String> mcdsIdList = (List<String>)reqMap.get("mcdsIdList");
            bookDAO.operateBookValidState(reqMap,mcdsIdList);
        }catch (Exception e){
            logger.error("更新书籍状态失败："+e);
            throw new Exception("更新书籍状态失败："+e);
        }
    }

    @Override
    public void updateBook(@RequestBody Map<String,Object> reqMap) throws Exception{
        try {
            String bookId = (String)reqMap.get("bookId");//创建书籍id
            String skuId = "";//库存id
            Map<String, Object> insertBookMap = new HashMap<>();
            insertBookMap.put("bookId", bookId);
            insertBookMap.put("bookName", reqMap.get("name"));
            insertBookMap.put("bookPrice", reqMap.get("price"));
            //insertBookMap.put("catgId", reqMap.get("type"));
            insertBookMap.put("describe", reqMap.get("describe"));
//            insertBookMap.put("shopId", reqMap.get("shopId"));
//            insertBookMap.put("shopName", reqMap.get("shopName"));
//            insertBookMap.put("skuId", skuId);
            insertBookMap.put("validState", "0");
            insertBookMap.put("userId", reqMap.get("userId"));
            bookDAO.updateBookInfo(insertBookMap);
            Map<String, Object> insertBookSku = new HashMap<>();
            List<String> bookIdList = new ArrayList<>();
            bookIdList.add(bookId);
            //修改商品信息不允许修改库存，只能通过加减库存按钮修改库存
//            List<Map<String,Object>> bookSkuList = orderDAO.selectBookSku(bookIdList);
//            if(null!=bookSkuList&&bookSkuList.size()>0){
//                skuId = bookSkuList.get(0).get("skuId").toString();
//                insertBookSku.put("version", bookSkuList.get(0).get("version"));
//                insertBookSku.put("skuId", skuId);
//                insertBookSku.put("skuTotal", reqMap.get("skuNum"));
//                insertBookSku.put("skuSale", 0);
//                insertBookSku.put("validState", "1");
//                insertBookSku.put("userId", reqMap.get("userId"));
//            }

//            Map<String, Object> insertSkuRL = new HashMap<>();
//            insertSkuRL.put("bookId", bookId);
//            insertSkuRL.put("skuId", skuId);
//            insertSkuRL.put("validState", "1");
//            insertSkuRL.put("userId", reqMap.get("userId"));
//            bookDAO.insertBookSkuRl(insertSkuRL);
            Map<String,Object> deletePic = new HashMap<>();
            deletePic.put("table","t_picture");
            deletePic.put("validState","0");
            deletePic.put("userId",reqMap.get("userId"));
            List<String> bookIdList01 = new ArrayList<>();
            bookIdList01.add(bookId);
            deletePic.put("bookIdList",bookIdList01);
            bookDAO.updatePicValidState(deletePic);
            Map<String, Object> insertPic = new HashMap<>();
            String pictureId = commonUtils.createAllId();
            insertPic.put("bookId", bookId);
            insertPic.put("pictureId", pictureId);
            insertPic.put("pictureUrl", reqMap.get("image"));
            insertPic.put("validState", "1");
            insertPic.put("userId", reqMap.get("userId"));
            bookDAO.insertBookPicture(insertPic);

            deletePic.put("table","t_book_big_picture");
            bookDAO.updatePicValidState(deletePic);
            String bigBookPic = commonUtils.createAllId();
            insertPic.put("bookId", bigBookPic);
            insertPic.put("pictureUrl", reqMap.get("detail"));
            bookDAO.insertBigBookPicture(insertPic);

            deletePic.put("table","t_book_image_small");
            bookDAO.updatePicValidState(deletePic);
            List<String> imageSmall = (List<String>) reqMap.get("imageSmall");
            if (null != imageSmall && imageSmall.size() > 0) {
                for (String eachImageSmall : imageSmall) {
                    Map<String, Object> eachInsertPic = new HashMap<>();
                    String eachPictureId = commonUtils.createAllId();
                    eachInsertPic.put("bookId", bookId);
                    eachInsertPic.put("pictureId", eachPictureId);
                    eachInsertPic.put("pictureUrl", eachImageSmall);
                    eachInsertPic.put("validState", "1");
                    eachInsertPic.put("userId", reqMap.get("userId"));
                    bookDAO.insertBookSmall(eachInsertPic);
                }
            }
        }catch (Exception e){
            logger.error("更新书籍信息失败："+e);
            throw new Exception("更新书籍信息失败："+e);
        }
    }

    public List<Map<String, Object>> queryAllCatg() throws Exception{
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = bookDAO.queryAllCatg();
        }catch (Exception e){
            logger.error("查询类型失败"+e);
        }
        return list;
    }

}
