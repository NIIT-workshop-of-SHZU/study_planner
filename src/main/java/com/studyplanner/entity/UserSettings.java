package com.studyplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户设置实体类
 */
@Data
public class UserSettings {
    
    /**
     * 设置ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 闲置时间阈值（分钟），超过此时间未打卡算闲置
     * 默认值：4320分钟（3天）
     */
    private Integer inactiveMinutes;
    
    /**
     * 提醒间隔（分钟），闲置后每隔多久提醒一次
     * 默认值：720分钟（12小时）
     */
    private Integer reminderIntervalMinutes;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

