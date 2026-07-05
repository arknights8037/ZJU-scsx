package com.smartcommunity.admin.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 —— 负责生成和解析 JWT Token。
 * <p>
 * 使用 HMAC-SHA256 签名，从配置文件中读取密钥和过期时间。
 * Token 中存放 userId（subject）和 phone（自定义声明）两个字段。
 */
@Component
public class JwtUtils {
    /** HMAC 签名密钥，从配置中的 jwt.secret 派生 */
    private final SecretKey key;
    /** Token 过期时间（毫秒），从配置中的 jwt.expiration 读取 */
    private final long expiration;

    /**
     * 构造 JWT 工具类。
     *
     * @param secret    JWT 签名密钥字符串（配置项 jwt.secret）
     * @param expiration Token 过期时长（毫秒）（配置项 jwt.expiration）
     */
    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration}") long expiration) {
        // 将 secret 字符串转为 HMAC-SHA256 密钥
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * 生成 JWT Token。
     *
     * @param userId 用户 ID，存入 subject 字段
     * @param phone  用户手机号，存入自定义声明 phone
     * @return 签名的 JWT 字符串
     */
    public String generateToken(Integer userId, String phone) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())          // 设置主体为用户 ID
                .claim("phone", phone)               // 附加手机号声明
                .issuedAt(now)                       // 签发时间
                .expiration(new Date(now.getTime() + expiration))  // 过期时间
                .signWith(key)                       // 使用 HMAC 密钥签名
                .compact();
    }

    /**
     * 从 JWT Token 中解析出用户 ID。
     *
     * @param token JWT 字符串
     * @return 用户 ID
     */
    public Integer getUserId(String token) {
        return Integer.parseInt(Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject());
    }
}
