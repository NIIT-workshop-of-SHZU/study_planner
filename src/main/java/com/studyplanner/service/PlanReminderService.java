package com.studyplanner.service;

import com.studyplanner.entity.StudyPlan;
import com.studyplanner.entity.User;
import com.studyplanner.mapper.CheckInMapper;
import com.studyplanner.mapper.PlanMapper;
import com.studyplanner.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 计划提醒服务类 - 定时检查闲置计划并发送邮件提醒
 */
@Service
public class PlanReminderService {
    
    @Autowired
    private PlanMapper planMapper;
    
    @Autowired
    private CheckInMapper checkInMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserSettingsService settingsService;
    
    // 记录每个计划的最后提醒时间（计划ID -> 最后提醒时间）
    private final Map<Long, LocalDateTime> lastReminderTime = new ConcurrentHashMap<>();
    
    /**
     * 定时检查闲置计划并发送提醒
     * 每1分钟执行一次，以支持用户自定义的提醒间隔
     * 生产环境可改为：fixedRate = 300000（每5分钟执行一次）
     */
    @Scheduled(fixedRate = 60000) // 1分钟执行一次（60000毫秒），支持最短1分钟的提醒间隔
    public void checkInactivePlans() {
        System.out.println("开始检查闲置计划...");
        
        try {
            // 获取所有进行中的计划
            List<StudyPlan> activePlans = planMapper.findAllActivePlans();
            
            for (StudyPlan plan : activePlans) {
                checkPlanInactivity(plan);
            }
            
            System.out.println("闲置计划检查完成，共检查 " + activePlans.size() + " 个计划");
        } catch (Exception e) {
            System.err.println("检查闲置计划时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 检查单个计划是否闲置
     */
    private void checkPlanInactivity(StudyPlan plan) {
        try {
            // 获取用户的闲置时间阈值
            int inactiveMinutes = settingsService.getInactiveMinutes(plan.getUserId());
            
            // 获取计划最后一次打卡时间
            LocalDateTime lastCheckInTime = checkInMapper.findLastCheckInDateTimeByPlanId(plan.getId());
            
            LocalDateTime now = LocalDateTime.now();
            long minutesSinceLastCheckIn;
            
            if (lastCheckInTime == null) {
                // 如果从未打卡，使用计划创建时间
                minutesSinceLastCheckIn = ChronoUnit.MINUTES.between(plan.getCreateTime(), now);
            } else {
                // 计算距离最后一次打卡的分钟数
                minutesSinceLastCheckIn = ChronoUnit.MINUTES.between(lastCheckInTime, now);
            }
            
            // 如果超过用户设置的闲置时间未打卡，发送提醒
            if (minutesSinceLastCheckIn >= inactiveMinutes) {
                sendReminder(plan, minutesSinceLastCheckIn);
            }
        } catch (Exception e) {
            System.err.println("检查计划闲置状态失败: " + plan.getId() + ", " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 发送提醒邮件
     */
    private void sendReminder(StudyPlan plan, long minutesSinceLastCheckIn) {
        try {
            // 获取用户的提醒间隔设置
            int reminderIntervalMinutes = settingsService.getReminderIntervalMinutes(plan.getUserId());
            
            // 检查距离上次提醒是否超过用户设置的间隔
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastReminder = lastReminderTime.get(plan.getId());
            
            if (lastReminder != null) {
                long minutesSinceLastReminder = ChronoUnit.MINUTES.between(lastReminder, now);
                long secondsSinceLastReminder = ChronoUnit.SECONDS.between(lastReminder, now);
                
                // 如果用户设置的间隔小于1分钟，使用秒数比较（支持更精确的间隔）
                if (reminderIntervalMinutes < 1) {
                    // 这种情况不应该发生，因为前端限制了最小值为1分钟
                    System.out.println(String.format(
                        "警告：计划[%s]的提醒间隔设置异常（%d分钟），使用默认1分钟",
                        plan.getTitle(), reminderIntervalMinutes
                    ));
                    reminderIntervalMinutes = 1;
                }
                
                if (minutesSinceLastReminder < reminderIntervalMinutes) {
                    // 距离上次提醒不足设置的间隔，跳过
                    System.out.println(String.format(
                        "计划[%s]距离上次提醒不足%d分钟（当前%d分钟%d秒），跳过提醒",
                        plan.getTitle(), reminderIntervalMinutes, minutesSinceLastReminder, 
                        secondsSinceLastReminder % 60
                    ));
                    return;
                }
            }
            
            // 获取用户信息
            User user = userMapper.findById(plan.getUserId());
            if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                System.out.println("用户 " + plan.getUserId() + " 没有邮箱，跳过提醒");
                return;
            }
            
            // 发送邮件提醒
            emailService.sendInactivePlanReminder(
                user.getEmail(),
                user.getUsername(),
                plan.getTitle()
            );
            
            // 记录本次提醒时间
            lastReminderTime.put(plan.getId(), now);
            
            System.out.println(String.format(
                "✓ 已发送闲置提醒: 计划[%s], 用户[%s], 已闲置%d分钟, 下次提醒间隔%d分钟",
                plan.getTitle(), user.getUsername(), minutesSinceLastCheckIn, reminderIntervalMinutes
            ));
        } catch (Exception e) {
            System.err.println("发送提醒邮件失败: " + plan.getId() + ", " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 清除计划的提醒记录（当计划有新打卡时调用）
     */
    public void clearReminderRecord(Long planId) {
        lastReminderTime.remove(planId);
        System.out.println("已清除计划 " + planId + " 的提醒记录");
    }
    
    /**
     * 手动触发检查（用于测试）
     */
    public void checkInactivePlansManually() {
        checkInactivePlans();
    }
}

