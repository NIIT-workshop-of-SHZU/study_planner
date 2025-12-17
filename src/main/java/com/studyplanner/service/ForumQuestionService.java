package com.studyplanner.service;

import com.studyplanner.entity.ForumQuestion;
import com.studyplanner.entity.ForumTopic;
import com.studyplanner.entity.ForumQuestionTopic;
import com.studyplanner.entity.User;
import com.studyplanner.mapper.ForumQuestionMapper;
import com.studyplanner.mapper.ForumQuestionTopicMapper;
import com.studyplanner.mapper.ForumTopicMapper;
import com.studyplanner.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ForumQuestionService {

    @Autowired
    private ForumQuestionMapper questionMapper;

    @Autowired
    private ForumTopicMapper topicMapper;

    @Autowired
    private ForumQuestionTopicMapper questionTopicMapper;

    @Autowired
    private UserMapper userMapper;

    public List<Map<String, Object>> listQuestions(Integer page, Integer pageSize, String keyword, Long topicId) {
        int p = (page == null || page < 1) ? 1 : page;
        int ps = (pageSize == null || pageSize < 1) ? 20 : pageSize;
        int offset = (p - 1) * ps;

        List<ForumQuestion> questions;
        if (topicId != null) {
            List<Long> ids = questionTopicMapper.findQuestionIdsByTopicId(topicId);
            if (ids == null || ids.isEmpty()) return new ArrayList<>();
            questions = questionMapper.findByIds(ids, offset, ps);
        } else {
            questions = questionMapper.findAll(offset, ps, keyword);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (ForumQuestion q : questions) {
            result.add(toQuestionMap(q));
        }
        return result;
    }

    public Map<String, Object> getQuestionDetail(Long id) {
        ForumQuestion q = questionMapper.findById(id);
        if (q == null) return null;

        // 浏览数 +1（最小实现）
        try {
            questionMapper.incrementViewCount(id);
            q.setViewCount((q.getViewCount() == null ? 0 : q.getViewCount()) + 1);
        } catch (Exception ignore) {}

        return toQuestionMap(q);
    }

    @Transactional
    public Map<String, Object> createQuestion(Map<String, Object> data, Long userId) {
        String title = asString(data.get("title"));
        String content = asString(data.get("content"));
        Integer anonymous = asInt(data.get("anonymous"));

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (content == null) content = "";

        ForumQuestion q = new ForumQuestion();
        q.setUserId(userId);
        q.setTitle(title.trim());
        q.setContent(content);
        q.setAnonymous(anonymous == null ? 0 : anonymous);
        q.setStatus(1);
        q.setViewCount(0);
        q.setFollowCount(0);
        q.setAnswerCount(0);
        q.setCreateTime(LocalDateTime.now());
        q.setUpdateTime(LocalDateTime.now());

        questionMapper.insert(q);

        // topic_ids（仅绑定已存在话题；不存在的 id 直接跳过，避免数据库对不上）
        List<Long> topicIds = asLongList(data.get("topic_ids"));
        if (topicIds != null) {
            for (Long tid : topicIds) {
                if (tid == null) continue;
                ForumTopic t = topicMapper.findById(tid);
                if (t == null) continue;

                ForumQuestionTopic rel = new ForumQuestionTopic();
                rel.setQuestionId(q.getId());
                rel.setTopicId(tid);
                rel.setCreateTime(LocalDateTime.now());
                questionTopicMapper.insert(rel);

                // 话题 question_count +1（最小实现）
                try { topicMapper.incrementQuestionCount(tid); } catch (Exception ignore) {}
            }
        }

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", q.getId());
        return resp;
    }

    public List<Map<String, Object>> getTopicsOfQuestion(Long questionId) {
        List<Long> topicIds = questionTopicMapper.findTopicIdsByQuestionId(questionId);
        if (topicIds == null || topicIds.isEmpty()) return new ArrayList<>();
        List<ForumTopic> topics = topicMapper.findByIds(topicIds);

        List<Map<String, Object>> result = new ArrayList<>();
        for (ForumTopic t : topics) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", t.getId());
            m.put("name", t.getName());
            m.put("description", t.getDescription());
            result.add(m);
        }
        return result;
    }

    private Map<String, Object> toQuestionMap(ForumQuestion q) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", q.getId());
        m.put("title", q.getTitle());
        m.put("content", q.getContent());

        m.put("created_at", q.getCreateTime() == null ? null : q.getCreateTime().toString());
        m.put("updated_at", q.getUpdateTime() == null ? null : q.getUpdateTime().toString());

        m.put("answer_count", q.getAnswerCount() == null ? 0 : q.getAnswerCount());
        m.put("view_count", q.getViewCount() == null ? 0 : q.getViewCount());
        m.put("follow_count", q.getFollowCount() == null ? 0 : q.getFollowCount());
        m.put("is_followed", false);

        // topics
        m.put("topics", getTopicsOfQuestion(q.getId()));

        // author（匿名则不给 author，前端会显示“匿名用户”）
        if (q.getAnonymous() != null && q.getAnonymous() == 1) {
            m.put("author", null);
        } else {
            User u = userMapper.findById(q.getUserId());
            m.put("author", toUserMap(u));
        }

        return m;
    }

    private Map<String, Object> toUserMap(User u) {
        if (u == null) return null;
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("username", u.getUsername());
        m.put("avatar", u.getAvatar());
        m.put("bio", null);
        return m;
    }

    private String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private Integer asInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return null; }
    }

    @SuppressWarnings("unchecked")
    private List<Long> asLongList(Object o) {
        if (o == null) return null;
        if (o instanceof List<?> list) {
            List<Long> out = new ArrayList<>();
            for (Object it : list) {
                Long v = asLong(it);
                if (v != null) out.add(v);
            }
            return out;
        }
        return null;
    }

    private Long asLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(o)); } catch (Exception e) { return null; }
    }
}
