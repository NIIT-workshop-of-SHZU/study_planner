package com.studyplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 */
@Data
public class ChatMessage {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}

