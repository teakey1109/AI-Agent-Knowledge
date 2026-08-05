package com.knowledge.base.common.utils;

import com.knowledge.base.common.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * <p>提供JWT Token的生成、解析和验证功能</p>
 *
 * @author fangAndlu
 */
@Slf4j
@Component
public class JwtTokenUtil {

    @Resource
    private JwtConfig jwtConfig;

    /**
     * 生成访问 Token
     *
     * @param userId 用户 ID
     * @return Token
     */
    public String generateAccessToken(Long userId) {
        return generateToken(userId, jwtConfig.getExpiration() * 1000);
    }

    /**
     * 生成刷新 Token
     *
     * @param userId 用户 ID
     * @return Token
     */
    public String generateRefreshToken(Long userId) {
        return generateToken(userId, jwtConfig.getRefreshExpiration() * 1000);
    }

    /**
     * 生成 Token
     *
     * @param userId 用户 ID
     * @param expiration 过期时间（毫秒）
     * @return Token
     */
    private String generateToken(Long userId, Long expiration) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "access");

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuer(jwtConfig.getIssuer())
                .issuedAt(now)
                .expiration(exp)
                .signWith(jwtConfig.secretKey())
                .compact();
    }

    /**
     * 解析 Token
     *
     * @param token Token
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtConfig.secretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析 token 失败：{}", e.getMessage());
            return null;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return null;
        }
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token Token
     * @return 是否有效
     */
    public boolean validToken(String token) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                return false;
            }
            Date expiration = claims.getExpiration();
            return expiration.after((new Date()));
        } catch (Exception e) {
            log.error("验证 Token 失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查 Token 是否即将过期
     *
     * @param token Token
     * @param thresholdSeconds 阈值（秒）
     * @return 是否即将过期
     */
    public boolean isTokenExpiringSoon(String token, int thresholdSeconds) {
        try {
            Claims claims = parseToken(token);
            if (claims == null) {
                return true;
            }
            Date expiration = claims.getExpiration();
            long timeToExpiry = expiration.getTime() - System.currentTimeMillis();
            return timeToExpiry < thresholdSeconds * 1000L;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 刷新 Token
     *
     * @param refreshToken 刷新 Token
     * @return 新的访问 Token
     */
    public String refreshToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        if (claims == null) {
            throw new RuntimeException("刷新 Token 无效");
        }

        Long userId = Long.parseLong(claims.getSubject());
        return generateAccessToken(userId);
    }
}
