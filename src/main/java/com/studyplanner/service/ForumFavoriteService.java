package com.studyplanner.service;

import com.studyplanner.mapper.forum.ForumQuestionFavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class ForumFavoriteService {
    
    @Autowired
    private ForumQuestionFavoriteMapper favoriteMapper;
    
    @Transactional
    public Map<String, Object> favoriteQuestion(Long questionId, Long userId) {
        if (questionId == null || userId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        
        boolean exists = favoriteMapper.exists(userId, questionId);
        if (exists) {
            // 已收藏，取消收藏
            favoriteMapper.delete(userId, questionId);
            int count = favoriteMapper.countByQuestionId(questionId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("is_favorited", false);
            resp.put("favorite_count", count);
            return resp;
        } else {
            // 未收藏，添加收藏
            favoriteMapper.insert(userId, questionId);
            int count = favoriteMapper.countByQuestionId(questionId);
            Map<String, Object> resp = new HashMap<>();
            resp.put("is_favorited", true);
            resp.put("favorite_count", count);
            return resp;
        }
    }
    
    public boolean isFavorited(Long questionId, Long userId) {
        if (questionId == null || userId == null) return false;
        return favoriteMapper.exists(userId, questionId);
    }
    
    public int getFavoriteCount(Long questionId) {
        if (questionId == null) return 0;
        return favoriteMapper.countByQuestionId(questionId);
    }
}




