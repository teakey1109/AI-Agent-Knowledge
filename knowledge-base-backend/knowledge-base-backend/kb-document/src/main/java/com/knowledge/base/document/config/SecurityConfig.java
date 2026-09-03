package com.knowledge.base.document.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 文档服务 Security 配置
 *
 * @author fangAndlu
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. 只放行明确不需要认证的接口
                        .requestMatchers("/documents/**", "/categories/**", "/tags/**", "/comments/**").permitAll()

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
