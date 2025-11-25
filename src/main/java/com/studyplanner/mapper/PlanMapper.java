package com.studyplanner.mapper;

import com.studyplanner.entity.StudyPlan;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 学习计划Mapper接口
 */
@Mapper
public interface PlanMapper {
    
    /**
     * 根据ID查询计划
     */
    @Select("SELECT * FROM study_plan WHERE id = #{id}")
    StudyPlan findById(Long id);
    
    /**
     * 查询用户的所有计划
     */
    @Select("SELECT * FROM study_plan WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<StudyPlan> findByUserId(Long userId);
    
    /**
     * 查询用户进行中的计划
     */
    @Select("SELECT * FROM study_plan WHERE user_id = #{userId} AND status = '进行中' ORDER BY create_time DESC")
    List<StudyPlan> findActiveByUserId(Long userId);
    
    /**
     * 插入新计划
     */
    @Insert("INSERT INTO study_plan (user_id, title, goal, level, daily_hours, total_days, start_date, end_date, status, create_time, update_time) " +
            "VALUES (#{userId}, #{title}, #{goal}, #{level}, #{dailyHours}, #{totalDays}, #{startDate}, #{endDate}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StudyPlan plan);
    
    /**
     * 更新计划
     */
    @Update("UPDATE study_plan SET title = #{title}, goal = #{goal}, status = #{status}, update_time = NOW() WHERE id = #{id}")
    int update(StudyPlan plan);
    
    /**
     * 更新计划状态
     */
    @Update("UPDATE study_plan SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 删除计划
     */
    @Delete("DELETE FROM study_plan WHERE id = #{id}")
    int delete(Long id);
}
