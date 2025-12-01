package com.studyplanner.controller;

import com.studyplanner.dto.ApiResponse;
import com.studyplanner.entity.UserSettings;
import com.studyplanner.service.UserSettingsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户设置控制器
 */
@RestController
@RequestMapping("/api/settings")
public class SettingsController {
    
    @Autowired
    private UserSettingsService settingsService;
    
    /**
     * 获取用户设置
     */
    @GetMapping
    public ApiResponse<UserSettings> getSettings(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        
        UserSettings settings = settingsService.getSettings(userId);
        return ApiResponse.success(settings);
    }
    
    /**
     * 更新用户设置
     */
    @PutMapping
    public ApiResponse<UserSettings> updateSettings(@RequestBody Map<String, Object> request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.unauthorized("请先登录");
        }
        
        try {
            Integer inactiveMinutes = null;
            Integer reminderIntervalMinutes = null;
            
            if (request.get("inactiveMinutes") != null) {
                inactiveMinutes = Integer.valueOf(request.get("inactiveMinutes").toString());
            }
            if (request.get("reminderIntervalMinutes") != null) {
                reminderIntervalMinutes = Integer.valueOf(request.get("reminderIntervalMinutes").toString());
            }
            
            // 验证参数范围
            if (inactiveMinutes != null && (inactiveMinutes < 1 || inactiveMinutes > 10080)) {
                return ApiResponse.error("闲置时间必须在1-10080分钟之间（1分钟到7天）");
            }
            if (reminderIntervalMinutes != null && (reminderIntervalMinutes < 1 || reminderIntervalMinutes > 10080)) {
                return ApiResponse.error("提醒间隔必须在1-10080分钟之间（1分钟到7天）");
            }
            
            UserSettings settings = settingsService.updateSettings(userId, inactiveMinutes, reminderIntervalMinutes);
            return ApiResponse.success("设置更新成功", settings);
        } catch (Exception e) {
            return ApiResponse.error("更新设置失败: " + e.getMessage());
        }
    }
}

