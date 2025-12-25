package com.studyplanner.service;

import com.studyplanner.entity.ForumTopic;
import com.studyplanner.mapper.forum.ForumTopicMapper;
import com.studyplanner.mapper.forum.ForumTopicFollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ForumTopicService {

    @Autowired
    private ForumTopicMapper topicMapper;
    
    @Autowired
    private ForumTopicFollowMapper topicFollowMapper;

    public List<Map<String, Object>> getHotTopics(int limit, Long currentUserId) {
        List<ForumTopic> topics = topicMapper.findHotTopics(limit);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ForumTopic t : topics) {
            boolean isFollowed = currentUserId != null && topicFollowMapper.exists(currentUserId, t.getId());
            result.add(toTopicMap(t, isFollowed));
        }
        return result;
    }

    public List<Map<String, Object>> getTopics(Integer page, Integer pageSize) {
        int p = (page == null || page < 1) ? 1 : page;
        int ps = (pageSize == null || pageSize < 1) ? 20 : pageSize;
        int offset = (p - 1) * ps;

        List<ForumTopic> topics = topicMapper.findAllPaged(offset, ps);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ForumTopic t : topics) result.add(toTopicMap(t, false));
        return result;
    }

    public Map<String, Object> getTopicDetail(Long id, Long currentUserId) {
        ForumTopic t = topicMapper.findById(id);
        if (t == null) return null;
        boolean isFollowed = currentUserId != null && topicFollowMapper.exists(currentUserId, id);
        return toTopicMap(t, isFollowed);
    }
    
    @org.springframework.transaction.annotation.Transactional
    public Map<String, Object> followTopic(Long topicId, Long userId) {
        if (topicId == null || userId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        boolean exists = topicFollowMapper.exists(userId, topicId);
        if (exists) {
            // 已关注，取消关注
            topicFollowMapper.delete(userId, topicId);
            topicFollowMapper.decrementFollowCount(topicId);
        } else {
            // 未关注，添加关注
            topicFollowMapper.insert(userId, topicId);
            topicFollowMapper.incrementFollowCount(topicId);
        }
        
        ForumTopic t = topicMapper.findById(topicId);
        return toTopicMap(t, !exists);
    }
    
    public List<Map<String, Object>> getMyFollowedTopics(Long userId, Integer page, Integer pageSize) {
        int p = (page == null || page < 1) ? 1 : page;
        int ps = (pageSize == null || pageSize < 1) ? 20 : pageSize;
        int offset = (p - 1) * ps;
        
        List<ForumTopic> topics = topicMapper.findFollowedByUserId(userId, offset, ps);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ForumTopic t : topics) result.add(toTopicMap(t, true));
        return result;
    }

    @org.springframework.transaction.annotation.Transactional
    public Map<String, Object> createOrGetTopic(String name, String description) {
        // 先查找是否已存在（不区分大小写）
        ForumTopic existing = topicMapper.findByName(name);
        if (existing != null) {
            return toTopicMap(existing, false);
        }
        
        // 不存在则创建
        ForumTopic topic = new ForumTopic();
        topic.setName(name);
        topic.setDescription(description == null ? "" : description);
        try {
            topicMapper.insert(topic);
            return toTopicMap(topic, false);
        } catch (Exception e) {
            // 如果插入失败（可能是唯一键冲突），再次查找
            existing = topicMapper.findByName(name);
            if (existing != null) {
                return toTopicMap(existing, false);
            }
            // 检查是否是唯一键冲突
            String errorMsg = e.getMessage();
            if (errorMsg != null && (errorMsg.contains("Duplicate") || errorMsg.contains("UNIQUE"))) {
                throw new IllegalArgumentException("话题名称已存在");
            }
            throw new RuntimeException("话题创建失败：" + errorMsg, e);
        }
    }

    public Long findTopicByName(String name) {
        ForumTopic t = topicMapper.findByName(name);
        return t == null ? null : t.getId();
    }

    private Map<String, Object> toTopicMap(ForumTopic t, boolean isFollowed) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", t.getId());
        m.put("name", t.getName());
        m.put("description", t.getDescription());
        m.put("follow_count", t.getFollowCount() == null ? 0 : t.getFollowCount());
        m.put("question_count", t.getQuestionCount() == null ? 0 : t.getQuestionCount());
        m.put("is_followed", isFollowed);
        return m;
    }
}
