package com.studyplanner.service;

import com.studyplanner.entity.ForumComment;
import com.studyplanner.entity.User;
import com.studyplanner.mapper.ForumAnswerMapper;
import com.studyplanner.mapper.ForumCommentMapper;
import com.studyplanner.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ForumCommentService {

    @Autowired
    private ForumCommentMapper commentMapper;

    @Autowired
    private ForumAnswerMapper answerMapper;

    @Autowired
    private UserMapper userMapper;

    public List<Map<String, Object>> listCommentsByAnswer(Long answerId) {
        List<ForumComment> all = commentMapper.findByAnswerId(answerId);

        // 先转成 map，方便组装 replies
        Map<Long, Map<String, Object>> idToMap = new HashMap<>();
        Map<Long, User> userCache = new HashMap<>();

        for (ForumComment c : all) {
            User u = userCache.computeIfAbsent(c.getUserId(), uid -> userMapper.findById(uid));
            Map<String, Object> cm = toCommentMap(c, u);
            cm.put("replies", new ArrayList<Map<String, Object>>());
            idToMap.put(c.getId(), cm);
        }

        // 组装层级：parent_id == null 为一级评论，否则进入 parent.replies
        List<Map<String, Object>> roots = new ArrayList<>();
        for (ForumComment c : all) {
            Map<String, Object> cm = idToMap.get(c.getId());
            Long parentId = c.getParentId();
            if (parentId == null) {
                roots.add(cm);
            } else {
                Map<String, Object> parent = idToMap.get(parentId);
                if (parent != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> replies = (List<Map<String, Object>>) parent.get("replies");

                    // 前端需要 reply.parent.author.username
                    Map<String, Object> parentStub = new HashMap<>();
                    parentStub.put("id", parentId);
                    parentStub.put("author", parent.get("author"));
                    cm.put("parent", parentStub);

                    replies.add(cm);
                } else {
                    // 找不到 parent 的情况：降级当根评论（避免前端空白）
                    roots.add(cm);
                }
            }
        }

        return roots;
    }

    @Transactional
    public Map<String, Object> createComment(Map<String, Object> data, Long userId) {
        Long answerId = asLong(data.get("answer_id"));
        String content = asString(data.get("content"));
        Long parentId = asLong(data.get("parent_id"));

        if (answerId == null) throw new IllegalArgumentException("answer_id 不能为空");
        if (content == null || content.trim().isEmpty()) throw new IllegalArgumentException("内容不能为空");

        ForumComment c = new ForumComment();
        c.setAnswerId(answerId);
        c.setUserId(userId);
        c.setContent(content.trim());
        c.setParentId(parentId);
        c.setVoteCount(0);
        c.setStatus(1);
        c.setCreateTime(LocalDateTime.now());
        c.setUpdateTime(LocalDateTime.now());

        commentMapper.insert(c);

        // answer.comment_count +1（最小实现）
        try { answerMapper.incrementCommentCount(answerId); } catch (Exception ignore) {}

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", c.getId());
        return resp;
    }

    public Map<String, Object> voteComment(Long id) {
        commentMapper.incrementVoteCount(id);

        ForumComment c = commentMapper.findById(id);
        Map<String, Object> resp = new HashMap<>();
        resp.put("vote_count", c == null || c.getVoteCount() == null ? 0 : c.getVoteCount());
        return resp;
    }

    private Map<String, Object> toCommentMap(ForumComment c, User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", c.getId());
        m.put("answer_id", c.getAnswerId());
        m.put("author_id", c.getUserId());
        m.put("content", c.getContent());
        m.put("parent_id", c.getParentId());
        m.put("created_at", c.getCreateTime() == null ? null : c.getCreateTime().toString());
        m.put("updated_at", c.getUpdateTime() == null ? null : c.getUpdateTime().toString());
        m.put("vote_count", c.getVoteCount() == null ? 0 : c.getVoteCount());
        m.put("author", toUserMap(u));
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

    private Long asLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(o)); } catch (Exception e) { return null; }
    }
}
