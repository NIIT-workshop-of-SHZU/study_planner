package com.studyplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI对话记录实体类
 */
@Data
public class ChatHistory {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户问题
     */
    private String question;
    
    /**
     * AI回答
     */
    private String answer;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
