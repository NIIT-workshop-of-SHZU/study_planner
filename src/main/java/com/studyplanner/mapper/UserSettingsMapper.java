package com.studyplanner.mapper;

import com.studyplanner.entity.UserSettings;
import org.apache.ibatis.annotations.*;

/**
 * 用户设置Mapper接口
 */
@Mapper
public interface UserSettingsMapper {
    
    /**
     * 根据用户ID查询设置
     */
    @Select("SELECT * FROM user_settings WHERE user_id = #{userId}")
    UserSettings findByUserId(Long userId);
    
    /**
     * 插入用户设置
     */
    @Insert("INSERT INTO user_settings (user_id, inactive_minutes, reminder_interval_minutes, create_time, update_time) " +
            "VALUES (#{userId}, #{inactiveMinutes}, #{reminderIntervalMinutes}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserSettings settings);
    
    /**
     * 更新用户设置
     */
    @Update("UPDATE user_settings SET inactive_minutes = #{inactiveMinutes}, " +
            "reminder_interval_minutes = #{reminderIntervalMinutes}, update_time = NOW() " +
            "WHERE user_id = #{userId}")
    int update(UserSettings settings);
    
    /**
     * 根据用户ID删除设置
     */
    @Delete("DELETE FROM user_settings WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}

