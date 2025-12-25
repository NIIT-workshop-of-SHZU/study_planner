package com.studyplanner.mapper.forum;

import com.studyplanner.entity.ForumAnswer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ForumAnswerCollectMapper {
    
    @Insert("INSERT INTO forum_answer_collect (user_id, answer_id, create_time) " +
            "VALUES (#{userId}, #{answerId}, NOW())")
    int insert(@Param("userId") Long userId, @Param("answerId") Long answerId);
    
    @Delete("DELETE FROM forum_answer_collect WHERE user_id = #{userId} AND answer_id = #{answerId}")
    int delete(@Param("userId") Long userId, @Param("answerId") Long answerId);
    
    @Select("SELECT COUNT(*) > 0 FROM forum_answer_collect WHERE user_id = #{userId} AND answer_id = #{answerId}")
    boolean exists(@Param("userId") Long userId, @Param("answerId") Long answerId);
    
    @Select("SELECT a.* FROM forum_answer a " +
            "INNER JOIN forum_answer_collect c ON a.id = c.answer_id " +
            "WHERE c.user_id = #{userId} " +
            "ORDER BY c.create_time DESC, a.id DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<ForumAnswer> findByUserId(
        @Param("userId") Long userId,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
}




