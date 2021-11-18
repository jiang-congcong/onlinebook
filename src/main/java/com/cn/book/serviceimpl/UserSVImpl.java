package com.cn.book.serviceimpl;

import com.cn.book.dao.UserDAO;
import com.cn.book.iservice.IUserSV;
import com.cn.book.utils.CommonUtils;
import com.xiaoleilu.hutool.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/10/27 19:38
 */
@Service
public class UserSVImpl implements IUserSV {
    public static Logger logger = LoggerFactory.getLogger(UserSVImpl.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CommonUtils commonUtils;

    @Override
    public void register(Map<String, Object> reqMap) throws Exception {
        if(null!=reqMap&&reqMap.size()>0) {
            try {
                userDAO.register(reqMap);
                userDAO.insertUserProfilePic(reqMap);
            }catch (Exception e){
                logger.error("用户注册失败："+e.getMessage());
                throw new Exception("-9999",e);
            }
        }
    }

    @Override
    public Map<String,Object> queryUserPasswordAndSalt(String userId) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        if(!StrUtil.hasEmpty(userId)){
            List<Map<String,Object>> resultList= userDAO.queryUserPassword(userId);
            if(null!=resultList&&resultList.size()>0){
                resultMap = resultList.get(0);
            }
        }
        return resultMap;
    }

    @Override
    public boolean checkUsernameIsRegister(String username) throws Exception{
        boolean result = true;
        if(!StrUtil.hasEmpty(username)){
            List<String> resultList = userDAO.checkUsernameIsRegister(username);
            if(null==resultList || resultList.size()==0){
                result = false;
            }
        }
        return result;
    }

    @Override
    public boolean updateUserInfo(Map<String,Object> reqMap) throws Exception{
        boolean result = true;
        if(null!=reqMap){
            try {
                boolean isNeedAddHeadImage = true;
                Map<String,Object> queryUserInfo = userDAO.queryUserInfo(reqMap.get("userId").toString());
                if(null!=queryUserInfo){
                    String headImage = (String)queryUserInfo.get("userImage");
                    if(!StrUtil.hasEmpty(headImage)){
                        isNeedAddHeadImage = false;
                    }
                }
                String userImage = (String)reqMap.get("userImage");

                if(!isNeedAddHeadImage&&null!=userImage&&userImage.length()>0){ //修改头像
                    userDAO.updateUserProfilePic(reqMap);
                }
                else if(isNeedAddHeadImage&&null!=userImage&&userImage.length()>0){ //插入头像
                    Map<String,Object> insertMap = new HashMap<>();
                    insertMap.put("userId",reqMap.get("userId"));
                    insertMap.put("pictureId",commonUtils.createAllId());
                    insertMap.put("validState","1");
                    insertMap.put("userImage",reqMap.get("userImage"));
                    userDAO.insertUserProfilePic(insertMap);
                }
                String username = (String)reqMap.get("username");
                if(null!=username&&username.length()>0){ //更新用户名
                    userDAO.updateUsername(reqMap);
                }
            }catch (Exception e){
                logger.error("更改用户头像失败！");
                result = false;
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> queryUserInfo(String userId) throws Exception{
        Map<String, Object> result = new HashMap<>();
        if(!StrUtil.hasEmpty(userId)) {
            try {
                result = userDAO.queryUserInfo(userId);
            } catch (Exception e) {
                logger.error("查询用户信息失败：" + e);
            }
        }
        return result;
    }
}
