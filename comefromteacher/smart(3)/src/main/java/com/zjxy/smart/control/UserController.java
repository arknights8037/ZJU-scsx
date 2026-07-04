package com.zjxy.smart.control;

import com.zjxy.smart.common.Result;
import com.zjxy.smart.model.User;
import com.zjxy.smart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("login")
    public Result login(@RequestBody User user){
        return userService.login(user);
    }
}
