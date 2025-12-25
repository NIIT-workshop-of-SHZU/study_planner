package com.studyplanner.service;

import com.studyplanner.entity.User;
import com.studyplanner.mapper.UserMapper;
import com.studyplanner.mapper.forum.ForumUserFollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ForumUserFollowService {
    
    @Autowired
    private ForumUserFollowMapper followMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Transactional
    public Map<String, Object> followUser(Long followeeId, Long followerId) {
        if (followeeId == null || followerId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        if (followeeId.equals(followerId)) {
            throw new IllegalArgumentException("不能关注自己");
        }
        
        boolean exists = followMapper.exists(followerId, followeeId);
        if (exists) {
            // 已关注，取消关注
            followMapper.delete(followerId, followeeId);
            int followerCount = followMapper.countFollowers(followeeId);
            int followingCount = followMapper.countFollowing(followerId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("is_following", false);
            resp.put("follower_count", followerCount);
            resp.put("following_count", followingCount);
            return resp;
        } else {
            // 未关注，添加关注
            followMapper.insert(followerId, followeeId);
            int followerCount = followMapper.countFollowers(followeeId);
            int followingCount = followMapper.countFollowing(followerId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("is_following", true);
            resp.put("follower_count", followerCount);
            resp.put("following_count", followingCount);
            return resp;
        }
    }
    
    public boolean isFollowing(Long followerId, Long followeeId) {
        if (followerId == null || followeeId == null) return false;
        if (followerId.equals(followeeId)) return false;
        return followMapper.exists(followerId, followeeId);
    }
    
    public int getFollowerCount(Long userId) {
        if (userId == null) return 0;
        return followMapper.countFollowers(userId);
    }
    
    public int getFollowingCount(Long userId) {
        if (userId == null) return 0;
        return followMapper.countFollowing(userId);
    }
    
    public List<Map<String, Object>> getFollowers(Long userId, Integer page, Integer pageSize) {
        int p = (page == null || page < 1) ? 1 : page;
        int ps = (pageSize == null || pageSize < 1) ? 20 : pageSize;
        int offset = (p - 1) * ps;
        
        List<User> users = followMapper.findFollowers(userId, offset, ps);
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("avatar", u.getAvatar());
            m.put("bio", null);
            m.put("created_at", u.getCreateTime() == null ? null : u.getCreateTime().toString());
            result.add(m);
        }
        return result;
    }
    
    public List<Map<String, Object>> getFollowing(Long userId, Integer page, Integer pageSize) {
        int p = (page == null || page < 1) ? 1 : page;
        int ps = (pageSize == null || pageSize < 1) ? 20 : pageSize;
        int offset = (p - 1) * ps;
        
        List<User> users = followMapper.findFollowing(userId, offset, ps);
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("avatar", u.getAvatar());
            m.put("bio", null);
            m.put("created_at", u.getCreateTime() == null ? null : u.getCreateTime().toString());
            result.add(m);
        }
        return result;
    }
}




