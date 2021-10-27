package com.cn.book.controller;

import com.cn.book.utils.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiangcongcong
 * @date 2021/10/25 20:14
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/register")
    @ResponseBody
    public Result register(){
        Result result = new Result();

        return result;
    }
}
