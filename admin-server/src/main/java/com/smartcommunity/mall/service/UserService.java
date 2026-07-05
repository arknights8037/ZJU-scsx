package com.smartcommunity.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcommunity.mall.config.JwtUtils;
import com.smartcommunity.mall.dto.PasswordChangeForm;
import com.smartcommunity.mall.dto.UserProfileForm;
import com.smartcommunity.mall.dto.UserProfileView;
import com.smartcommunity.mall.entity.User;
import com.smartcommunity.mall.entity.UserResidentProfile;
import com.smartcommunity.mall.mapper.UserMapper;
import com.smartcommunity.mall.mapper.UserResidentProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 居民端用户服务。
 *
 * user 表负责"账号身份"：手机号、密码、昵称、头像等；
 * user_resident_profile 表负责"社区住户资料"：楼栋、单元、门牌号、紧急联系人等。
 * 两者拆开后，既能兼容老师原始的 user 表，也能逐步扩展社区业务字段。
 */
@Service
@RequiredArgsConstructor
public class UserService {

    // 基础账号信息 mapper，映射数据库中的 user 表。
    private final UserMapper userMapper;
    // 住户扩展资料 mapper，映射 user_resident_profile 表。
    private final UserResidentProfileMapper residentProfileMapper;
    // BCrypt 密码编码器，由 SecurityConfig 注入。
    private final PasswordEncoder passwordEncoder;
    // JWT 工具，用于登录成功后签发 Token。
    private final JwtUtils jwtUtils;

    public String login(String phone, String password) {
        // 手机号是居民端登录账号，因此先按 phone 查唯一用户。
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
        // 注册前做手机号唯一性校验，避免同一个手机号出现多个账号。
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

    public UserProfileView getProfile(Integer userId) {
        // 个人中心展示时需要同时返回账号资料和住户资料，所以统一组装成 UserProfileView。
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return toProfileView(user, getResidentProfile(userId));
    }

    /**
     * 修改当前登录用户的个人资料。
     * 使用表单 DTO 逐个赋值，避免前端偷偷修改 id、手机号、用户状态等字段。
     */
    public UserProfileView updateProfile(Integer userId, UserProfileForm form) {
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

        // 住户资料不是每个老用户都有；第一次保存资料时自动创建扩展记录。
        UserResidentProfile profile = getResidentProfile(userId);
        if (profile == null) {
            profile = new UserResidentProfile();
            profile.setUserId(userId);
        }
        profile.setOwnerName(trimToNull(form.getOwnerName()));
        profile.setBuildingNo(trimToNull(form.getBuildingNo()));
        profile.setUnitNo(trimToNull(form.getUnitNo()));
        profile.setRoomNo(trimToNull(form.getRoomNo()));
        profile.setFullAddress(trimToNull(form.getFullAddress()));
        profile.setEmergencyContact(trimToNull(form.getEmergencyContact()));
        profile.setEmergencyPhone(trimToNull(form.getEmergencyPhone()));
        residentProfileMapper.insertOrUpdate(profile);
        return toProfileView(user, profile);
    }

    /** 上传接口返回图片地址后立即落库，避免用户刷新页面时丢失新头像。 */
    public String updateAvatar(Integer userId, String avatar) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        String value = trimToNull(avatar);
        if (value != null && !value.startsWith("/mall-uploads/")) {
            throw new RuntimeException("头像地址无效");
        }
        user.setAvatar(value);
        userMapper.updateById(user);
        return value;
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

    private UserResidentProfile getResidentProfile(Integer userId) {
        // user_resident_profile.user_id 有唯一索引；limit 1 是额外兜底，避免脏数据导致 selectOne 抛错。
        return residentProfileMapper.selectOne(new LambdaQueryWrapper<UserResidentProfile>()
            .eq(UserResidentProfile::getUserId, userId)
            .last("limit 1"));
    }

    private UserProfileView toProfileView(User user, UserResidentProfile profile) {
        // DTO 只暴露前端需要展示/编辑的字段，避免把 userPassword 等敏感字段返回给浏览器。
        UserProfileView view = new UserProfileView();
        view.setId(user.getId());
        view.setPhone(user.getPhone());
        view.setUserName(user.getUserName());
        view.setSex(user.getSex());
        view.setMail(user.getMail());
        view.setAvatar(user.getAvatar());
        view.setUserStatus(user.getUserStatus());
        view.setLastLoginTime(user.getLastLoginTime());
        view.setCreateTime(user.getCreateTime());
        view.setUpdateTime(user.getUpdateTime());
        if (profile != null) {
            view.setOwnerName(profile.getOwnerName());
            view.setBuildingNo(profile.getBuildingNo());
            view.setUnitNo(profile.getUnitNo());
            view.setRoomNo(profile.getRoomNo());
            view.setFullAddress(profile.getFullAddress());
            view.setEmergencyContact(profile.getEmergencyContact());
            view.setEmergencyPhone(profile.getEmergencyPhone());
        }
        return view;
    }

    private String trimToNull(String value) {
        // 数据库中统一用 null 表示"未填写"，避免保存一堆空字符串。
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
