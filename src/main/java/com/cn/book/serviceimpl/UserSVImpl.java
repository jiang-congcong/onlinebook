package com.cn.book.serviceimpl;

import com.cn.book.dao.UserDAO;
import com.cn.book.iservice.IUserSV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
