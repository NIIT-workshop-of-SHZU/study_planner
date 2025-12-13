package com.studyplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WebSocket消息响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    
    /**
     * 消息类型
     */
    private String type;
    
    /**
     * 消息载荷
     */
    private Object payload;
    
    /**
     * 消息载荷（新消息）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessagePayload {
        private Long id;
        private Long user_id;
        private String username;
        private String avatar;
        private String content;
        private LocalDateTime created_at;
    }
    
    /**
     * 用户加入/离开载荷
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserEventPayload {
        private Long user_id;
        private String username;
        private java.util.List<UserInfo> users;
    }
    
    /**
     * 在线用户信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
    }
    
    /**
     * 错误载荷
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorPayload {
        private String message;
    }
}

