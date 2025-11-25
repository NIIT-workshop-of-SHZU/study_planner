package com.studyplanner.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 打卡记录实体类
 */
@Data
public class CheckIn {
    
    /**
     * 打卡ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 计划ID
     */
    private Long planId;
    
    /**
     * 任务详情ID
     */
    private Long detailId;
    
    /**
     * 打卡日期
     */
    private LocalDate checkDate;
    
    /**
     * 实际学习时长(小时)
     */
    private BigDecimal studyHours;
    
    /**
     * 学习心得
     */
    private String note;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
