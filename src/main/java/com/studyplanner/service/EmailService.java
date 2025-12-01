package com.studyplanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务类
 */
@Service
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String fromEmail;
    
    /**
     * 发送简单文本邮件
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        if (mailSender == null || fromEmail == null || fromEmail.isEmpty()) {
            System.out.println("邮件服务未配置，跳过发送邮件到: " + to);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            System.out.println("邮件发送成功: " + to);
        } catch (Exception e) {
            System.err.println("邮件发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 发送闲置计划提醒邮件
     */
    public void sendInactivePlanReminder(String to, String username, String planTitle) {
        String subject = "【学习计划提醒】您的计划已闲置多日";
        String text = String.format(
            "亲爱的 %s，\n\n" +
            "我们注意到您的学习计划《%s》已经超过3分钟未打卡了。\n\n" +
            "学习是一个持续的过程，每天坚持一点点，就能看到显著的进步！\n\n" +
            "赶快回来继续您的学习之旅吧！\n\n" +
            "祝学习愉快！\n\n" +
            "智能学习计划生成器",
            username, planTitle
        );
        
        sendSimpleEmail(to, subject, text);
    }
}

