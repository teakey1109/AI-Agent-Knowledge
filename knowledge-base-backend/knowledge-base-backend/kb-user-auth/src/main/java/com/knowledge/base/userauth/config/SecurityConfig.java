package com.knowledge.base.userauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 👇 关键修改：使用相对路径，放行 /users 下的所有请求
                        .requestMatchers("/users/**").permitAll()

                        // 放行 Knife4j 接口文档（同样使用相对路径）
                        .requestMatchers("/doc.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 必须禁用 CSRF，否则 POST/PUT 等请求会失败
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
