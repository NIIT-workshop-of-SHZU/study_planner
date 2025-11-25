package com.studyplanner.mapper;

import com.studyplanner.entity.PlanDetail;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 计划详情Mapper接口
 */
@Mapper
public interface PlanDetailMapper {
    
    /**
     * 根据ID查询详情
     */
    @Select("SELECT * FROM plan_detail WHERE id = #{id}")
    PlanDetail findById(Long id);
    
    /**
     * 根据计划ID查询所有任务
     */
    @Select("SELECT * FROM plan_detail WHERE plan_id = #{planId} ORDER BY day_number")
    List<PlanDetail> findByPlanId(Long planId);
    
    /**
     * 根据计划ID和天数查询任务
     */
    @Select("SELECT * FROM plan_detail WHERE plan_id = #{planId} AND day_number = #{dayNumber}")
    PlanDetail findByPlanIdAndDay(@Param("planId") Long planId, @Param("dayNumber") Integer dayNumber);
    
    /**
     * 批量插入任务
     */
    int batchInsert(List<PlanDetail> details);
    
    /**
     * 插入单个任务
     */
    @Insert("INSERT INTO plan_detail (plan_id, day_number, content, duration, resources, is_completed, create_time) " +
            "VALUES (#{planId}, #{dayNumber}, #{content}, #{duration}, #{resources}, #{isCompleted}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PlanDetail detail);
    
    /**
     * 更新完成状态
     */
    @Update("UPDATE plan_detail SET is_completed = #{isCompleted} WHERE id = #{id}")
    int updateCompleted(@Param("id") Long id, @Param("isCompleted") Integer isCompleted);
    
    /**
     * 根据计划ID删除所有任务
     */
    @Delete("DELETE FROM plan_detail WHERE plan_id = #{planId}")
    int deleteByPlanId(Long planId);
    
    /**
     * 统计计划完成的任务数
     */
    @Select("SELECT COUNT(*) FROM plan_detail WHERE plan_id = #{planId} AND is_completed = 1")
    int countCompletedByPlanId(Long planId);
}
