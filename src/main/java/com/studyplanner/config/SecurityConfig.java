package com.studyplanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * 安全过滤链配置
     * 注意：这里配置为允许所有请求，实际生产环境需要根据需求配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（前后端分离项目通常禁用）
            .csrf(csrf -> csrf.disable())
            // 允许所有请求（使用Session管理登录状态）
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/user/login", "/api/user/register").permitAll()
                .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/pages/**", "/images/**").permitAll()
                .anyRequest().permitAll()  // 开发阶段允许所有请求
            )
            // 禁用默认登录页面
            .formLogin(form -> form.disable())
            // 完全禁用HTTP基本认证，避免浏览器弹出认证对话框
            .httpBasic(basic -> basic.disable())
            // 禁用默认的认证入口点，避免自动添加WWW-Authenticate头
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    // 自定义401响应，不添加WWW-Authenticate头
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\"}");
                })
            );
        
        return http.build();
    }
}
