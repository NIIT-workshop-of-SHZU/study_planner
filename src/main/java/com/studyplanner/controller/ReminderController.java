package com.studyplanner.controller;

import com.studyplanner.dto.ApiResponse;
import com.studyplanner.service.PlanReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提醒控制器 - 用于测试和手动触发
 */
@RestController
@RequestMapping("/api/reminder")
public class ReminderController {
    
    @Autowired
    private PlanReminderService planReminderService;
    
    /**
     * 手动触发检查闲置计划（用于测试）
     */
    @PostMapping("/check")
    public ApiResponse<String> checkInactivePlans() {
        try {
            planReminderService.checkInactivePlansManually();
            return ApiResponse.success("检查完成，请查看控制台日志和邮箱");
        } catch (Exception e) {
            return ApiResponse.error("检查失败: " + e.getMessage());
        }
    }
}

