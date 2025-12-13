package com.studyplanner.dto;

import lombok.Data;

/**
 * WebSocket消息请求DTO
 */
@Data
public class ChatMessageRequest {
    
    /**
     * 消息类型
     */
    private String type;
    
    /**
     * 消息载荷
     */
    private Payload payload;
    
    @Data
    public static class Payload {
        /**
         * 消息内容
         */
        private String content;
    }
}

