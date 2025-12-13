package com.studyplanner.mapper;

import com.studyplanner.entity.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聊天消息Mapper接口
 */
@Mapper
public interface ChatMessageMapper {
    
    /**
     * 插入新消息
     */
    @Insert("INSERT INTO chat_message (user_id, username, avatar, content, created_at) " +
            "VALUES (#{userId}, #{username}, #{avatar}, #{content}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ChatMessage message);
    
    /**
     * 获取历史消息（按时间倒序）
     */
    @Select("SELECT * FROM chat_message " +
            "WHERE (#{before} IS NULL OR created_at < #{before}) " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    List<ChatMessage> findRecentMessages(
            @Param("before") LocalDateTime before,
            @Param("limit") int limit
    );
    
    /**
     * 获取消息总数
     */
    @Select("SELECT COUNT(*) FROM chat_message")
    long count();
}

