package com.knowledge.base.common.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户上下文工具类
 *
 * <p>提供用户上下文获取和设置功能
 * 在一个 HTTP 请求的处理链路中，随时随地获取当前登录用户的信息，
 * 而不需要在 Controller、Service、DAO 之间层层传递用户参数。
 * </p>
 *
 * @author fangAndlu
 */
public class UserContextUtil {

    private final static ThreadLocal<Long>  USER_ID_HOLDER = new ThreadLocal<>();
    private final static ThreadLocal<String>  USERNAME_HOLDER = new ThreadLocal<>();
    private final static ThreadLocal<String>  TOKEN_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户 ID
     * @param userId 用户 ID
     */
    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    /**
     * 获取当前用户 ID
     * @return 用户 ID
     */
    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    /**
     * 设置当前用户名
     * @param username 用户名
     */
    public static void setUsername(String username) {
        USERNAME_HOLDER.set(username);
    }

    /**
     * 获取当前用户名
     * @return 用户名
     */
    public static String getUsername() {
        return USERNAME_HOLDER.get();
    }

    /**
     * 设置 Token
     * @param token Token
     */
    public static void setToken(String token) {
        TOKEN_HOLDER.set(token);
    }

    /**
     * 获取 Token
     * @return Token
     */
    public static String getToken() {
        return TOKEN_HOLDER.get();
    }

    /**
     * 从请求中获取用户 ID
     * @param request HttpServletRequest
     * @return 用户 ID
     */
    public static Long getUserIdFromRequest (HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            return null;
        }

        try {
            // 使用 JwtTokenUtil 解析 token
            JwtTokenUtil jwtTokenUtil = SpringContextUtil.getBean(JwtTokenUtil.class);
            return jwtTokenUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 这个方法负责从 HTTP 请求头（Header）中获取身份验证令牌。
     * 它首先尝试从请求头中获取 Authorization 字段的值。
     * 在标准的 JWT 认证中，该字段的格式通常为 Bearer <token>。
     * 因此，代码会判断获取到的值是否以 Bearer 开头，如果是，则截取掉前缀（即从第 7 个字符开始截取），返回真正的 Token 字符串；否则返回 null
     * @param request HttpServletRequest
     * @return Token
     */
    public static String extractTokenFromRequest (HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 清除当前用户上下文
     */
    public static void clear() {
        USER_ID_HOLDER.remove();
        USERNAME_HOLDER.remove();
        TOKEN_HOLDER.remove();
    }

    /**
     * 检查用户是否已登录
     * @return 是否已登录
     */
    public static boolean isLoggedIn() {
        return getUserId() != null;
    }
}
