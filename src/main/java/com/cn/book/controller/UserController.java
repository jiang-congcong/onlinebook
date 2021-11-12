package com.cn.book.controller;

import com.cn.book.iservice.IUserSV;
import com.cn.book.utils.CommonUtils;
import com.cn.book.utils.RedisOperationUtils;
import com.cn.book.utils.Result;
import com.xiaoleilu.hutool.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiangcongcong
 * @date 2021/10/25 20:14
 */
@RestController
@RequestMapping("/user")
@Api(value = "/user",description = "用户类")
public class UserController {

    private static int hashTime = 4096;
    public static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private IUserSV iUserSV;

    @Autowired
    private CommonUtils commonUtils;

    @Autowired
    private RedisOperationUtils redisOperationUtils;


    @RequestMapping(method = RequestMethod.POST,value = "/register")
    @ResponseBody
    @ApiOperation(value = "用户注册")
    public Result register(@RequestBody Map<String,Object> reqMap){
        Result result = new Result();
        String username = (String)reqMap.get("username");
        String password = (String)reqMap.get("password");
        String phoneNum = (String)reqMap.get("phoneNum");
        if(StrUtil.hasEmpty(username)||StrUtil.hasEmpty(password)||StrUtil.hasEmpty(phoneNum)){
            result.setRtnCode("400");
            result.setRtnMessage("用户名或密码或手机号不能为空！");
            return result;
        }
        try{
            //undo用户名唯一性校验
            boolean isRegister = iUserSV.checkUsernameIsRegister(username);
            if(isRegister){
                result.setRtnCode("400");
                result.setRtnMessage("此用户名已被注册，请换用户名注册！");
                return result;
            }
            String userId = commonUtils.createAllId(); //生成用户id
            String salt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String md5Password = commonUtils.passwordMD5HashSalt(password,salt,hashTime);
            reqMap.put("password",md5Password);
            reqMap.put("salt",salt);
            reqMap.put("userId",userId);
            reqMap.put("validState","1");
            iUserSV.register(reqMap);
            String token = commonUtils.createAndUpdateToken(userId); //生成token，放入redis中，并返回给前端
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("token",token);
            resultMap.put("userId",userId);
            result.setResult(resultMap);
            result.setRtnCode("200");
        }catch (Exception e){
            logger.error("用户注册失败");
            result.setRtnCode("400");
            result.setRtnMessage("用户注册失败");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/login")
    @ResponseBody
    @ApiOperation(value = "用户登录")
    public Result login(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        String username = (String)reqMap.get("username");
        String password = (String)reqMap.get("password");
        if(StrUtil.hasEmpty(username)||StrUtil.hasEmpty(password)){
            result.setRtnCode("400");
            result.setRtnMessage("用户名或密码不能为空！");
        }
        else{
            Map<String,Object> queryUserInfoMap = iUserSV.queryUserPasswordAndSalt(username);
            if(null!=queryUserInfoMap&&queryUserInfoMap.size()>0){
                String getPassword = queryUserInfoMap.get("password").toString();
                String getSalt = queryUserInfoMap.get("salt").toString();
                String userId = queryUserInfoMap.get("userId").toString();
                String equaleStr = commonUtils.passwordMD5HashSalt(password,getSalt,hashTime);
                if(getPassword.equals(equaleStr)){
                    result.setRtnCode("200");
                    Map<String,Object> resultMap = new HashMap<>();
                    String token = commonUtils.createAndUpdateToken(userId);
                    try {
                        Map<String, Object> userInfo = iUserSV.queryUserInfo(userId);
                        if(null!=userInfo&&userInfo.size()>0){
                            String image = (String)userInfo.get("userImage");
                            image = commonUtils.dealImageTobase64(image);
                            resultMap.put("username",userInfo.get("username"));
                            resultMap.put("userImage",image);
                        }
                    }catch (Exception e){
                        logger.error("查询用户信息失败"+e);
                    }
                    resultMap.put("userId",userId);
                    resultMap.put("token",token);
                    result.setRtnMessage("登陆成功！");
                    result.setResult(resultMap);
                }
                else{
                    result.setRtnCode("400");
                    result.setRtnMessage("密码错误！");
                }
            }
            else{
                result.setRtnCode("400");
                result.setRtnMessage("用户未注册！");
            }
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/userinfo")
    @ResponseBody
    @ApiOperation(value = "用户状态")
    public Result userInfo(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        String token = (String)reqMap.get("token");
        if(!StrUtil.hasEmpty(token)&&null!=redisOperationUtils.get(token)){
            String userId = redisOperationUtils.get(token).toString();
            int overTime = 24*60*60;
            redisOperationUtils.set(token,userId,overTime);
            Map<String,Object> resultMap = new HashMap<>();
            try {
                Map<String, Object> userInfo = iUserSV.queryUserInfo(userId);
                if(null!=userInfo&&userInfo.size()>0){
                    String image = (String)userInfo.get("userImage");
                    image = commonUtils.dealImageTobase64(image);
                    resultMap.put("username",userInfo.get("username"));
                    resultMap.put("userImage",image);
                }
            }catch (Exception e){
                logger.error("查询用户信息失败"+e);
            }
            resultMap.put("token",token);
            resultMap.put("userId",userId);
            result.setResult(resultMap);
            result.setRtnCode("200");
        }
        else{
            result.setRtnCode("400");
            result.setRtnMessage("用户未登录！");
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/logout")
    @ResponseBody
    @ApiOperation(value = "用户退出登录")
    public Result logout(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        String token = (String)reqMap.get("token");
        if(!StrUtil.hasEmpty(token)){
            try {
                redisOperationUtils.del(token);
                result.setRtnCode("200");
            }catch (Exception e){
                logger.error("退出登录失败！");
                result.setRtnMessage("退出登录失败！");
                result.setRtnCode("400");
            }
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/updateUserInfo")
    @ResponseBody
    @ApiOperation(value = "用户头像修改")
    public Result updateUserInfo(@RequestBody Map<String,Object> reqMap) throws Exception {
        Result result = new Result();
        String userId = (String)reqMap.get("userId");
        String username = (String)reqMap.get("username");
        String userImage = (String)reqMap.get("userImage");
        if(StrUtil.hasEmpty(userId)||StrUtil.hasEmpty(username)||StrUtil.hasEmpty(userImage)){
            result.setRtnCode("400");
            result.setRtnMessage("用户id或图片地址不能为空！");
            return result;
        }
        //图片转路径
        String imagePath = commonUtils.dealbase64ToImagePath(userImage);
        reqMap.put("userImage",imagePath);
        boolean isUpdateSuccess = iUserSV.updateUserInfo(reqMap);
        if(isUpdateSuccess){
            result.setRtnMessage("更新用户头像成功！");
            result.setRtnCode("200");
        }
        else{
            result.setRtnMessage("更新用户头像失败！");
            result.setRtnCode("400");
        }
        return result;
    }

}
