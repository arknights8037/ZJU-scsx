package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.mall.config.JwtUtils;
import com.smartcommunity.mall.dto.PasswordChangeForm;
import com.smartcommunity.mall.dto.UserProfileForm;
import com.smartcommunity.mall.entity.User;
import com.smartcommunity.mall.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

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
        // 登录成功后记录时间，个人中心会展示这个字段。
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
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

    /**
     * 修改当前登录用户的个人资料。
     * 使用表单 DTO 逐个赋值，避免前端偷偷修改 id、手机号、用户状态等字段。
     */
    public User updateProfile(Integer userId, UserProfileForm form) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!StringUtils.hasText(form.getUserName())) {
            throw new RuntimeException("用户名不能为空");
        }
        user.setUserName(form.getUserName().trim());
        user.setSex(StringUtils.hasText(form.getSex()) ? form.getSex().trim() : null);
        user.setMail(StringUtils.hasText(form.getMail()) ? form.getMail().trim() : null);
        user.setAvatar(StringUtils.hasText(form.getAvatar()) ? form.getAvatar().trim() : null);
        userMapper.updateById(user);
        return user;
    }

    /**
     * 修改密码前先验证原密码，新密码仍然使用 BCrypt 加密后再保存。
     */
    public void changePassword(Integer userId, PasswordChangeForm form) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!StringUtils.hasText(form.getOldPassword())
                || !passwordEncoder.matches(form.getOldPassword(), user.getUserPassword())) {
            throw new RuntimeException("原密码不正确");
        }
        if (!StringUtils.hasText(form.getNewPassword()) || form.getNewPassword().length() < 6) {
            throw new RuntimeException("新密码不能少于6位");
        }
        if (passwordEncoder.matches(form.getNewPassword(), user.getUserPassword())) {
            throw new RuntimeException("新密码不能与原密码相同");
        }
        user.setUserPassword(passwordEncoder.encode(form.getNewPassword()));
        userMapper.updateById(user);
    }
}
