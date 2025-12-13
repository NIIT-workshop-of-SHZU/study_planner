package com.studyplanner.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket认证拦截器
 * 用于在WebSocket握手时进行用户认证
 */
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            
            // 尝试从URL参数获取token
            String query = servletRequest.getServletRequest().getQueryString();
            String token = null;
            if (query != null && query.contains("token=")) {
                token = query.substring(query.indexOf("token=") + 6);
                if (token.contains("&")) {
                    token = token.substring(0, token.indexOf("&"));
                }
            }
            
            // 如果session中有userId，则允许连接
            if (session != null) {
                Long userId = (Long) session.getAttribute("userId");
                if (userId != null) {
                    attributes.put("userId", userId);
                    System.out.println("WebSocket握手: 设置 userId=" + userId + " 到 attributes (sessionId=" + session.getId() + ")");
                    return true;
                } else {
                    System.out.println("WebSocket握手: session 中没有 userId (sessionId=" + session.getId() + ")");
                }
            } else {
                System.out.println("WebSocket握手: session 为 null，尝试创建新session");
                // 尝试创建新session
                session = servletRequest.getServletRequest().getSession(true);
                Long userId = (Long) session.getAttribute("userId");
                if (userId != null) {
                    attributes.put("userId", userId);
                    System.out.println("WebSocket握手: 从新session获取 userId=" + userId);
                    return true;
                }
            }
            
            // 如果有token参数，尝试从session验证
            // 简化实现：当前使用session认证
            // 生产环境应该实现JWT token验证
            if (token != null && !token.isEmpty()) {
                // 这里应该验证JWT token并获取userId
                // 当前简化实现：如果有token且session中有userId，则允许连接
                if (session != null) {
                    Long userId = (Long) session.getAttribute("userId");
                    if (userId != null) {
                        attributes.put("userId", userId);
                        return true;
                    }
                }
            }
        }
        
        // 开发环境：允许连接（生产环境应拒绝未认证的连接）
        // return false; // 生产环境取消注释此行
        return true; // 开发环境允许所有连接
    }
    
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手后的处理
    }
}

