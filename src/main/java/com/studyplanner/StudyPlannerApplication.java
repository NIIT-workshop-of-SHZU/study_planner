package com.studyplanner;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智能学习计划生成器 - 启动类
 * Smart Study Planner Application
 */
@SpringBootApplication
@MapperScan("com.studyplanner.mapper")
public class StudyPlannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyPlannerApplication.class, args);
        System.out.println("========================================");
        System.out.println("  智能学习计划生成器 启动成功！");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
