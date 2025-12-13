package com.studyplanner.controller;

import com.studyplanner.dto.ApiResponse;
import com.studyplanner.dto.ChatMessageRequest;
import com.studyplanner.dto.ChatMessageResponse;
import com.studyplanner.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    /**
     * WebSocket消息处理
     * 客户端发送消息到 /app/chat/message
     */
    @MessageMapping("/chat/message")
    public void handleMessage(
            @Payload ChatMessageRequest request,
            SimpMessageHeaderAccessor headerAccessor) {
        
        // 从Session中获取用户ID
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        Long userId = sessionAttributes != null ? (Long) sessionAttributes.get("userId") : null;
        String sessionId = headerAccessor.getSessionId();
        
        System.out.println("收到消息: sessionId=" + sessionId + ", userId=" + userId + ", content=" + 
            (request.getPayload() != null ? request.getPayload().getContent() : "null"));
        
        if (userId == null) {
            System.out.println("警告: 收到消息但 userId 为 null, sessionId=" + sessionId);
            System.out.println("Session attributes: " + (sessionAttributes != null ? sessionAttributes.keySet() : "null"));
            // 无法发送错误消息，因为不知道用户ID
            return;
        }
        
        if ("message".equals(request.getType()) && request.getPayload() != null) {
            String content = request.getPayload().getContent();
            if (content != null && !content.trim().isEmpty()) {
                System.out.println("处理消息: userId=" + userId + ", content=" + content);
                chatService.sendMessage(userId, content.trim());
            }
        }
    }
    
    /**
     * 获取在线用户列表
     */
    @GetMapping("/online-users")
    public ApiResponse<List<ChatMessageResponse.UserInfo>> getOnlineUsers() {
        List<ChatMessageResponse.UserInfo> users = chatService.getOnlineUsers();
        return ApiResponse.success(users);
    }
    
    /**
     * 获取历史消息
     */
    @GetMapping("/messages")
    public ApiResponse<Map<String, Object>> getMessages(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer pageSize,
            @RequestParam(required = false) String before) {
        
        try {
            java.time.LocalDateTime beforeTime = null;
            if (before != null && !before.isEmpty()) {
                beforeTime = java.time.LocalDateTime.parse(before);
            }
            
            List<ChatMessageResponse.MessagePayload> messages = chatService.getHistoryMessages(beforeTime, pageSize);
            long total = chatService.getTotalMessageCount();
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", messages);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取历史消息失败: " + e.getMessage());
        }
    }
}

