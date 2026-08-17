package com.knowledge.base.foundation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 基础服务 Security 配置
 *
 * <p>配置说明：</p>
 * <ul>
 *   <li>禁用 CSRF：使用 JWT Token 不需要 CSRF 保护</li>
 *   <li>无状态会话：不使用 Session，完全依赖 JWT Token</li>
 *   <li>禁用 CORS：由网关统一处理，业务服务禁用 CORS</li>
 *   <li>WebSocket 端点：HTTP 层放行，认证由 STOMP 层 WebSocketAuthInterceptor 处理</li>
 * </ul>
 *
 * <p>WebSocket 认证机制：</p>
 * <ol>
 *   <li>客户端通过 SockJS 连接 {@code /ws/notification}</li>
 *   <li>STOMP CONNECT 帧携带 {@code Authorization: Bearer <token>}</li>
 *   <li>{@link WebSocketAuthInterceptor()} 在 STOMP 层验证 JWT 并设置 Principal</li>
 *   <li>服务端通过 {@code convertAndSendToUser(userId, ...)} 进行点对点推送</li>
 *   <li>匿名连接（无 Token）仅可接收广播消息（/topic/**）</li>
 * </ol>
 *
 * @author fangAndlu
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Resource
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Resource
//    private CustomAuthenticationEntryPoint authEntryPoint;
//
//    @Resource
//    private CustomAccessDeniedHandler accessDeniedHandler;
//
//    /**
//     * 配置安全过滤链
//     */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // 禁用CSRF保护（使用JWT不需要）
//                .csrf(AbstractHttpConfigurer::disable)
//
//                // 配置无状态会话（不使用Session）
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                // 禁用CORS（由网关统一处理）
//                .cors(AbstractHttpConfigurer::disable)
//
//                // 添加JWT认证过滤器
//                .addFilterBefore(jwtAuthenticationFilter,
//                        org.springframework.security.web.authentication
//                                .UsernamePasswordAuthenticationFilter.class)
//
//                // 配置授权规则
//                .authorizeHttpRequests(auth -> auth
//                        // ===== WebSocket端点：HTTP层完全放行 =====
//                        // WebSocket认证由STOMP层的WebSocketAuthInterceptor处理
//                        // SockJS的HTTP握手请求（/ws/notification/info等）也需要放行
//                        .requestMatchers("/ws/**").permitAll()
//
//                        // ===== API文档和监控：完全开放 =====
//                        .requestMatchers("/doc.html", "/swagger-resources/**",
//                                "/v3/api-docs/**", "/webjars/**").permitAll()
//                        .requestMatchers("/actuator/**").permitAll()
//
//                        // ===== OPTIONS预检请求：完全开放 =====
//                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//
//                        // ===== 通知接口：需要认证 =====
//                        .requestMatchers("/notifications/**").authenticated()
//
//                        // ===== 公开配置接口：无需认证（登录页/注册页需要读取） =====
//                        .requestMatchers("/config/public").permitAll()
//
//                        // ===== 其他请求：需要认证 =====
//                        .anyRequest().authenticated()
//                )
//
//                // 配置异常处理：返回JSON格式的401/403响应
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(authEntryPoint)
//                        .accessDeniedHandler(accessDeniedHandler)
//                );
//
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. 只放行明确不需要认证的接口
                        .requestMatchers("/dicts/**", "/notifications/**", "/config/**").permitAll()

                        // 2. OPTIONS 请求放行
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()


                )
                // 必须禁用 CSRF，否则 POST/PUT 等请求会失败
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
