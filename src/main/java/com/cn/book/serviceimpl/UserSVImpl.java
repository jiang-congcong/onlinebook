package com.cn.book.serviceimpl;

import com.cn.book.dao.UserDAO;
import com.cn.book.iservice.IUserSV;
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
    @Override
    public void register(Map<String, Object> reqMap) throws Exception {
        if(null!=reqMap&&reqMap.size()>0) {
            try {
                userDAO.register(reqMap);
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
}
