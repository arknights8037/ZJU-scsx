package com.zjxy.smart.service;

import com.zjxy.smart.common.Result;
import com.zjxy.smart.mapper.UserMapper;
import com.zjxy.smart.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     *
     * @param user
     * @return 0:登录成功 1:用户名错误 2：密码错误
     */
    public Result login(User user){
        Result result = new Result();
        int code = 0;

        User user1 = userMapper.selByName(user);
        if(user1==null){
            code = 1;
        }else if(!user1.getUserPassword().equals(user.getUserPassword())){
            code = 2;
        }
        result.setCode(code);
        result.setData(user1);
        return result;
    }

}
