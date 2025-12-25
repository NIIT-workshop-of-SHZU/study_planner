package com.studyplanner.controller;

import com.studyplanner.dto.ApiResponse;
import com.studyplanner.service.ForumAnswerService;
import com.studyplanner.service.ForumCommentService;
import com.studyplanner.service.ForumQuestionService;
import com.studyplanner.service.ForumTopicService;
import com.studyplanner.service.ForumFavoriteService;
import com.studyplanner.service.ForumUserFollowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumQuestionService questionService;

    @Autowired
    private ForumAnswerService answerService;

    @Autowired
    private ForumCommentService commentService;

    @Autowired
    private ForumTopicService topicService;
    
    @Autowired
    private ForumFavoriteService favoriteService;
    
    @Autowired
    private ForumUserFollowService userFollowService;


    
    // -------------------- Question --------------------

    @GetMapping("/question")
    public ApiResponse<List<Map<String, Object>>> getQuestions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Boolean following,
            @RequestParam(required = false) Boolean favorite,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        return ApiResponse.success(questionService.listQuestions(page, pageSize, keyword, topicId, sort, following, favorite, userId));
    }

    @GetMapping("/question/{id}")
    public ApiResponse<Map<String, Object>> getQuestionDetail(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Map<String, Object> q = questionService.getQuestionDetail(id, userId);
        if (q == null) return ApiResponse.error(404, "问题不存在");
        return ApiResponse.success(q);
    }

    @PostMapping("/question")
    public ApiResponse<Map<String, Object>> createQuestion(
            @RequestBody Map<String, Object> data,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(questionService.createQuestion(data, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("发布问题失败: " + e.getMessage());
        }
    }

    @PutMapping("/question/{id}")
    public ApiResponse<Map<String, Object>> updateQuestion(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(questionService.updateQuestion(id, data, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新问题失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/question/{id}")
    public ApiResponse<Void> deleteQuestion(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            questionService.deleteQuestion(id, userId);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除问题失败: " + e.getMessage());
        }
    }

    @PostMapping("/question/{id}/follow")
    public ApiResponse<Map<String, Object>> followQuestion(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        // 最小实现：不做用户级关注表，只返回结构，避免前端报错
        Map<String, Object> resp = new HashMap<>();
        resp.put("follow_count", 0);
        resp.put("is_followed", true);
        return ApiResponse.success(resp);
    }
    
    @PostMapping("/question/{id}/vote")
    public ApiResponse<Map<String, Object>> voteQuestion(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(questionService.voteQuestion(id, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("点赞失败: " + e.getMessage());
        }
    }

    // -------------------- Answer --------------------

    @GetMapping("/question/{questionId}/answers")
    public ApiResponse<List<Map<String, Object>>> getAnswers(
            @PathVariable Long questionId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String sort,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        return ApiResponse.success(answerService.listAnswers(questionId, page, pageSize, sort, userId));
    }

    @PostMapping("/answer")
    public ApiResponse<Map<String, Object>> createAnswer(@RequestBody Map<String, Object> data, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(answerService.createAnswer(data, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("发布回答失败: " + e.getMessage());
        }
    }

    @PostMapping("/answer/{id}/vote")
    public ApiResponse<Map<String, Object>> voteAnswer(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(answerService.voteAnswer(id, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("点赞失败: " + e.getMessage());
        }
    }

    @GetMapping("/answer/{id}")
    public ApiResponse<Map<String, Object>> getAnswerDetail(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Map<String, Object> a = answerService.getAnswerDetail(id, userId);
        if (a == null) return ApiResponse.error(404, "回答不存在");
        return ApiResponse.success(a);
    }

    @PutMapping("/answer/{id}")
    public ApiResponse<Map<String, Object>> updateAnswer(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(answerService.updateAnswer(id, data, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新回答失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/answer/{id}")
    public ApiResponse<Void> deleteAnswer(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            answerService.deleteAnswer(id, userId);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除回答失败: " + e.getMessage());
        }
    }

    @PostMapping("/answer/{id}/collect")
    public ApiResponse<Map<String, Object>> collectAnswer(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(answerService.collectAnswer(id, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("收藏失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/question/{id}/favorite")
    public ApiResponse<Map<String, Object>> favoriteQuestion(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");
        
        try {
            return ApiResponse.success(favoriteService.favoriteQuestion(id, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("操作失败: " + e.getMessage());
        }
    }

    // -------------------- Comment --------------------

    @GetMapping("/comment")
    public ApiResponse<List<Map<String, Object>>> getComments(
            @RequestParam(value = "answer_id") Long answerId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        return ApiResponse.success(commentService.listCommentsByAnswer(answerId, userId));
    }

    @PostMapping("/comment")
    public ApiResponse<Map<String, Object>> createComment(@RequestBody Map<String, Object> data, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(commentService.createComment(data, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("发表评论失败: " + e.getMessage());
        }
    }

    @PutMapping("/comment/{id}")
    public ApiResponse<Map<String, Object>> updateComment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(commentService.updateComment(id, data, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("更新评论失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/comment/{id}")
    public ApiResponse<Void> deleteComment(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            commentService.deleteComment(id, userId);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("删除评论失败: " + e.getMessage());
        }
    }

    @PostMapping("/comment/{id}/vote")
    public ApiResponse<Map<String, Object>> voteComment(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(commentService.voteComment(id, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("点赞失败: " + e.getMessage());
        }
    }

    // -------------------- Topic --------------------

    @GetMapping("/topic")
    public ApiResponse<List<Map<String, Object>>> getTopics(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        return ApiResponse.success(topicService.getTopics(page, pageSize));
    }

    @GetMapping("/topic/{id}")
    public ApiResponse<Map<String, Object>> getTopicDetail(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Map<String, Object> t = topicService.getTopicDetail(id, userId);
        if (t == null) return ApiResponse.error(404, "话题不存在");
        return ApiResponse.success(t);
    }

    @GetMapping("/topic/{id}/questions")
    public ApiResponse<List<Map<String, Object>>> getTopicQuestions(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        return ApiResponse.success(questionService.listQuestions(page, pageSize, null, id, null, null, null, userId));
    }

    @GetMapping("/topic/hot")
    public ApiResponse<List<Map<String, Object>>> getHotTopics(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        return ApiResponse.success(topicService.getHotTopics(10, userId));
    }

    @PostMapping("/topic")
    public ApiResponse<Map<String, Object>> createTopic(
            @RequestBody Map<String, Object> data,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        String name = (String) data.get("name");
        String description = (String) data.get("description");
        
        if (name == null || name.trim().isEmpty()) {
            return ApiResponse.badRequest("话题名称不能为空");
        }

        try {
            Map<String, Object> topic = topicService.createOrGetTopic(name.trim(), description);
            return ApiResponse.success(topic);
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            // 检查错误消息
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("已存在")) {
                // 尝试查找已存在的话题
                Long topicId = topicService.findTopicByName(name.trim());
                if (topicId != null) {
                    // 使用已定义的userId变量（第338行已定义）
                    Map<String, Object> existing = topicService.getTopicDetail(topicId, userId);
                    if (existing != null) {
                        return ApiResponse.success(existing);
                    }
                }
                return ApiResponse.badRequest("话题名称已存在");
            }
            return ApiResponse.error("创建话题失败: " + errorMsg);
        }
    }

    @PostMapping("/topic/{id}/follow")
    public ApiResponse<Map<String, Object>> followTopic(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");
        
        try {
            return ApiResponse.success(topicService.followTopic(id, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("操作失败: " + e.getMessage());
        }
    }

    // -------------------- User --------------------

    @GetMapping("/user/{id}")
    public ApiResponse<Map<String, Object>> getUserInfo(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Map<String, Object> user = questionService.getUserInfo(id, userId);
        if (user == null) return ApiResponse.error(404, "用户不存在");
        return ApiResponse.success(user);
    }

    @GetMapping("/user/{id}/questions")
    public ApiResponse<List<Map<String, Object>>> getUserQuestions(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        return ApiResponse.success(questionService.getUserQuestions(id, page, pageSize));
    }

    @GetMapping("/user/{id}/answers")
    public ApiResponse<List<Map<String, Object>>> getUserAnswers(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        return ApiResponse.success(answerService.getUserAnswers(id, page, pageSize));
    }

    @GetMapping("/user/{id}/collections")
    public ApiResponse<List<Map<String, Object>>> getUserCollections(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        // 返回收藏的帖子列表
        return ApiResponse.success(questionService.getUserFavorites(id, page, pageSize));
    }

    @PostMapping("/user/{id}/follow")
    public ApiResponse<Map<String, Object>> followUser(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");
        
        try {
            return ApiResponse.success(userFollowService.followUser(id, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("操作失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{id}/followers")
    public ApiResponse<List<Map<String, Object>>> getFollowers(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        return ApiResponse.success(questionService.getFollowers(id, page, pageSize));
    }

    @GetMapping("/user/{id}/following")
    public ApiResponse<List<Map<String, Object>>> getFollowing(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        return ApiResponse.success(questionService.getFollowing(id, page, pageSize));
    }

    // -------------------- Search --------------------

    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        return ApiResponse.success(questionService.search(keyword, type, sort, page, pageSize));
    }

    @GetMapping("/search/suggest")
    public ApiResponse<List<String>> getSuggestions(@RequestParam String keyword) {
        return ApiResponse.success(questionService.getSuggestions(keyword));
    }

    // -------------------- My Content --------------------

    @GetMapping("/my/questions")
    public ApiResponse<List<Map<String, Object>>> getMyQuestions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");
        return ApiResponse.success(questionService.getUserQuestions(userId, page, pageSize));
    }

    @GetMapping("/my/answers")
    public ApiResponse<List<Map<String, Object>>> getMyAnswers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");
        return ApiResponse.success(answerService.getUserAnswers(userId, page, pageSize));
    }

    @GetMapping("/my/collections")
    public ApiResponse<List<Map<String, Object>>> getMyCollections(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");
        // 返回收藏的帖子列表
        return ApiResponse.success(questionService.getUserFavorites(userId, page, pageSize));
    }

    @GetMapping("/my/following")
    public ApiResponse<Map<String, Object>> getMyFollowing(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");
        return ApiResponse.success(questionService.getMyFollowing(userId, page, pageSize));
    }
    
    @GetMapping("/my/followers")
    public ApiResponse<List<Map<String, Object>>> getMyFollowers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");
        return ApiResponse.success(userFollowService.getFollowers(userId, page, pageSize));
    }
}
