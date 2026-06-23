package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.mall.config.JwtUtils;
import com.smartcommunity.mall.entity.User;
import com.smartcommunity.mall.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public String login(String phone, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            throw new RuntimeException("密码错误");
        }
        return jwtUtils.generateToken(user.getId(), phone);
    }

    public User register(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, user.getPhone());
        if (userMapper.selectOne(wrapper) != null) {
            throw new RuntimeException("手机号已注册");
        }
        user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        user.setUserStatus(1);
        userMapper.insert(user);
        return user;
    }

    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return userMapper.selectOne(wrapper);
    }

    public User getById(Integer id) {
        return userMapper.selectById(id);
    }
}
