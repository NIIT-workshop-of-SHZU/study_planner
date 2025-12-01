package com.studyplanner.service;

import com.studyplanner.entity.UserSettings;
import com.studyplanner.mapper.UserSettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户设置服务类
 */
@Service
public class UserSettingsService {
    
    @Autowired
    private UserSettingsMapper settingsMapper;
    
    // 默认闲置时间：4320分钟（3天）
    private static final int DEFAULT_INACTIVE_MINUTES = 4320;
    
    // 默认提醒间隔：720分钟（12小时）
    private static final int DEFAULT_REMINDER_INTERVAL_MINUTES = 720;
    
    /**
     * 获取用户设置，如果不存在则创建默认设置
     */
    public UserSettings getOrCreateSettings(Long userId) {
        UserSettings settings = settingsMapper.findByUserId(userId);
        
        if (settings == null) {
            // 创建默认设置
            settings = new UserSettings();
            settings.setUserId(userId);
            settings.setInactiveMinutes(DEFAULT_INACTIVE_MINUTES);
            settings.setReminderIntervalMinutes(DEFAULT_REMINDER_INTERVAL_MINUTES);
            settingsMapper.insert(settings);
        }
        
        return settings;
    }
    
    /**
     * 获取用户设置
     */
    public UserSettings getSettings(Long userId) {
        return getOrCreateSettings(userId);
    }
    
    /**
     * 更新用户设置
     */
    @Transactional
    public UserSettings updateSettings(Long userId, Integer inactiveMinutes, Integer reminderIntervalMinutes) {
        UserSettings settings = getOrCreateSettings(userId);
        
        if (inactiveMinutes != null) {
            settings.setInactiveMinutes(inactiveMinutes);
        }
        if (reminderIntervalMinutes != null) {
            settings.setReminderIntervalMinutes(reminderIntervalMinutes);
        }
        
        settingsMapper.update(settings);
        return settings;
    }
    
    /**
     * 获取用户的闲置时间阈值（分钟）
     */
    public int getInactiveMinutes(Long userId) {
        UserSettings settings = getOrCreateSettings(userId);
        return settings.getInactiveMinutes() != null ? settings.getInactiveMinutes() : DEFAULT_INACTIVE_MINUTES;
    }
    
    /**
     * 获取用户的提醒间隔（分钟）
     */
    public int getReminderIntervalMinutes(Long userId) {
        UserSettings settings = getOrCreateSettings(userId);
        return settings.getReminderIntervalMinutes() != null ? settings.getReminderIntervalMinutes() : DEFAULT_REMINDER_INTERVAL_MINUTES;
    }
}

