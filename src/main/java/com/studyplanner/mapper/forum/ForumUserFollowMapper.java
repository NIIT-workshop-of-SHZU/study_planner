package com.studyplanner.mapper.forum;

import com.studyplanner.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ForumUserFollowMapper {
    
    @Insert("INSERT INTO forum_user_follow (follower_id, followee_id, create_time) " +
            "VALUES (#{followerId}, #{followeeId}, NOW())")
    int insert(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);
    
    @Delete("DELETE FROM forum_user_follow WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    int delete(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);
    
    @Select("SELECT COUNT(*) > 0 FROM forum_user_follow WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    boolean exists(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);
    
    @Select("SELECT COUNT(*) FROM forum_user_follow WHERE followee_id = #{followeeId}")
    int countFollowers(@Param("followeeId") Long followeeId);
    
    @Select("SELECT COUNT(*) FROM forum_user_follow WHERE follower_id = #{followerId}")
    int countFollowing(@Param("followerId") Long followerId);
    
    @Select("SELECT u.* FROM user u " +
            "INNER JOIN forum_user_follow f ON u.id = f.follower_id " +
            "WHERE f.followee_id = #{followeeId} " +
            "ORDER BY f.create_time DESC, u.id DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<User> findFollowers(
        @Param("followeeId") Long followeeId,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
    
    @Select("SELECT u.* FROM user u " +
            "INNER JOIN forum_user_follow f ON u.id = f.followee_id " +
            "WHERE f.follower_id = #{followerId} " +
            "ORDER BY f.create_time DESC, u.id DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    List<User> findFollowing(
        @Param("followerId") Long followerId,
        @Param("offset") int offset,
        @Param("limit") int limit
    );
}




