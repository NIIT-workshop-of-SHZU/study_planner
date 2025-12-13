package com.studyplanner.config;

import com.studyplanner.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;
import java.util.logging.Logger;

/**
 * WebSocket事件监听器
 * 处理用户连接和断开事件
 */
@Component
public class WebSocketEventListener {
    
    private static final Logger logger = Logger.getLogger(WebSocketEventListener.class.getName());
    
    @Autowired
    private ChatService chatService;
    
    /**
     * 用户连接事件（WebSocket连接建立）
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        logger.info("WebSocket连接建立，Session ID: " + sessionId);
    }
    
    /**
     * 用户订阅事件（STOMP订阅建立，此时可以获取userId）
     */
    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        Long userId = sessionAttributes != null ? (Long) sessionAttributes.get("userId") : null;
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();
        
        logger.info("用户订阅: sessionId=" + sessionId + ", userId=" + userId + ", destination=" + destination);
        if (sessionAttributes != null) {
            logger.info("Session attributes: " + sessionAttributes.keySet());
        } else {
            logger.warning("Session attributes 为 null");
        }
        
        // 当用户订阅 /topic/chat 时，触发用户加入
        if (userId != null && "/topic/chat".equals(destination)) {
            logger.info("触发用户加入: userId=" + userId);
            chatService.userJoined(userId);
            // 发送在线用户列表给新连接的用户（通过广播，所有订阅者都能收到）
            chatService.broadcastOnlineUsers();
        } else if (userId == null) {
            logger.warning("用户订阅但 userId 为 null，无法触发用户加入");
        }
    }
    
    /**
     * 用户断开事件
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        String sessionId = headerAccessor.getSessionId();
        
        logger.info("WebSocket断开连接，Session ID: " + sessionId + ", userId: " + userId);
        
        if (userId != null) {
            chatService.userLeft(userId);
        }
    }
}

