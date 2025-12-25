package com.studyplanner.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 计划生成请求DTO
 */
@Data
public class PlanGenerateRequest {
    
    @NotBlank(message = "学习目标不能为空")
    @Size(max = 500, message = "学习目标描述不能超过500字")
    private String goal;
    
    @NotBlank(message = "基础水平不能为空")
    private String level;  // 零基础/初级/中级/高级
    
    @NotNull(message = "每日学习时长不能为空")
    @DecimalMin(value = "0.5", message = "每日学习时长至少0.5小时")
    @DecimalMax(value = "12", message = "每日学习时长不能超过12小时")
    private BigDecimal dailyHours;
    
    @NotNull(message = "计划天数不能为空")
    @Min(value = 1, message = "计划天数至少1天")
    @Max(value = 365, message = "计划天数不能超过365天")
    private Integer totalDays;
    
    /**
     * 计划标题（可选，不填则自动生成）
     */
    @Size(max = 100, message = "标题不能超过100字")
    private String title;
    
    // ======= 自定义API配置（游客模式可用）=======
    
    /**
     * 自定义API基础URL
     */
    private String customApiUrl;
    
    /**
     * 自定义API Key
     */
    private String customApiKey;
    
    /**
     * 选择的模型名称
     */
    private String modelName;
    
    /**
     * 语言设置（zh-CN 或 en-US），用于生成对应语言的提示词
     */
    private String language;
}
