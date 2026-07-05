package com.smartcommunity.mall.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 —— 负责商城端 JWT Token 的生成和解析。
 * <p>
 * 使用 HMAC-SHA256 签名，从配置文件中读取密钥和过期时间。
 * 与 admin 模块的 JwtUtils 功能一致，额外提供 parseToken 方法返回完整 Claims。
 */
@Component
public class JwtUtils {

    /** HMAC 签名密钥 */
    private final SecretKey key;
    /** Token 过期时间（毫秒） */
    private final long expiration;

    /**
     * 构造 JWT 工具类。
     *
     * @param secret    JWT 签名密钥（配置项 jwt.secret）
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
     * @param userId 用户 ID
     * @param phone  用户手机号
     * @return 签名的 JWT 字符串
     */
    public String generateToken(Integer userId, String phone) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())          // 主体为用户 ID
                .claim("phone", phone)               // 附加手机号
                .issuedAt(now)                       // 签发时间
                .expiration(new Date(now.getTime() + expiration))  // 过期时间
                .signWith(key)                       // HMAC 签名
                .compact();
    }

    /**
     * 解析 Token 并获取完整 Claims。
     *
     * @param token JWT 字符串
     * @return 解析后的 Claims 负载
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中提取用户 ID。
     *
     * @param token JWT 字符串
     * @return 用户 ID
     */
    public Integer getUserId(String token) {
        return Integer.parseInt(parseToken(token).getSubject());
    }
}
