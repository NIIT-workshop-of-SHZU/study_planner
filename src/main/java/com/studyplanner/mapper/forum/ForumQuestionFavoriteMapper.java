package com.studyplanner.mapper.forum;

import com.studyplanner.entity.ForumQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ForumQuestionFavoriteMapper {
    
    @Insert("INSERT INTO forum_question_favorite (user_id, question_id, create_time) " +
            "VALUES (#{userId}, #{questionId}, NOW())")
    int insert(@Param("userId") Long userId, @Param("questionId") Long questionId);
    
    @Delete("DELETE FROM forum_question_favorite WHERE user_id = #{userId} AND question_id = #{questionId}")
    int delete(@Param("userId") Long userId, @Param("questionId") Long questionId);
    
    @Select("SELECT COUNT(*) > 0 FROM forum_question_favorite WHERE user_id = #{userId} AND question_id = #{questionId}")
    boolean exists(@Param("userId") Long userId, @Param("questionId") Long questionId);
    
    @Select("SELECT COUNT(*) FROM forum_question_favorite WHERE question_id = #{questionId}")
    int countByQuestionId(@Param("questionId") Long questionId);
    
    @Select("SELECT q.* FROM forum_question q " +
            "INNER JOIN forum_question_favorite f ON q.id = f.question_id " +
            "WHERE f.user_id = #{userId} " +
            "ORDER BY f.create_time DESC, q.id DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<ForumQuestion> findFavoritesByUserId(
        @Param("userId") Long userId,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
}




