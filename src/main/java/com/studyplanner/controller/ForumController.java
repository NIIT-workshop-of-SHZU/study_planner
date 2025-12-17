package com.studyplanner.controller;

import com.studyplanner.dto.ApiResponse;
import com.studyplanner.service.ForumAnswerService;
import com.studyplanner.service.ForumCommentService;
import com.studyplanner.service.ForumQuestionService;
import com.studyplanner.service.ForumTopicService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumQuestionService questionService;

    @Autowired
    private ForumTopicService topicService;

    @Autowired
    private ForumAnswerService answerService;

    @Autowired
    private ForumCommentService commentService;

    // -------------------- Question --------------------

    @GetMapping("/question")
    public ApiResponse<List<Map<String, Object>>> getQuestions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long topicId
    ) {
        return ApiResponse.success(questionService.listQuestions(page, pageSize, keyword, topicId));
    }

    @GetMapping("/question/{id}")
    public ApiResponse<Map<String, Object>> getQuestionDetail(@PathVariable Long id) {
        Map<String, Object> q = questionService.getQuestionDetail(id);
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

    @PostMapping("/question/{id}/follow")
    public ApiResponse<Map<String, Object>> followQuestion(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        // 最小实现：不做用户级关注表，只返回一个“已关注”的结构，避免前端报错
        Map<String, Object> resp = new HashMap<>();
        resp.put("follow_count", 0);
        resp.put("is_followed", true);
        return ApiResponse.success(resp);
    }

    // -------------------- Answer --------------------

    @GetMapping("/question/{questionId}/answers")
    public ApiResponse<List<Map<String, Object>>> getAnswers(
            @PathVariable Long questionId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(answerService.listAnswers(questionId, page, pageSize, sort));
    }

    @PostMapping("/answer")
    public ApiResponse<Map<String, Object>> createAnswer(
            @RequestBody Map<String, Object> data,
            HttpSession session
    ) {
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
            return ApiResponse.success(answerService.voteAnswer(id));
        } catch (Exception e) {
            return ApiResponse.error("点赞失败: " + e.getMessage());
        }
    }

    @PostMapping("/answer/{id}/collect")
    public ApiResponse<Map<String, Object>> collectAnswer(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(answerService.collectAnswer(id));
        } catch (Exception e) {
            return ApiResponse.error("收藏失败: " + e.getMessage());
        }
    }

    // -------------------- Comment --------------------

    @GetMapping("/comment")
    public ApiResponse<List<Map<String, Object>>> getComments(
            @RequestParam(value = "answer_id", required = false) Long answerId,
            @RequestParam(value = "question_id", required = false) Long questionId
    ) {
        // 前端当前用的是 answer_id
        if (answerId == null) return ApiResponse.success(new ArrayList<>());
        return ApiResponse.success(commentService.listCommentsByAnswer(answerId));
    }

    @PostMapping("/comment")
    public ApiResponse<Map<String, Object>> createComment(
            @RequestBody Map<String, Object> data,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(commentService.createComment(data, userId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("评论失败: " + e.getMessage());
        }
    }

    @PostMapping("/comment/{id}/vote")
    public ApiResponse<Map<String, Object>> voteComment(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        try {
            return ApiResponse.success(commentService.voteComment(id));
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
    public ApiResponse<Map<String, Object>> getTopicDetail(@PathVariable Long id) {
        Map<String, Object> t = topicService.getTopicDetail(id);
        if (t == null) return ApiResponse.error(404, "话题不存在");
        return ApiResponse.success(t);
    }

    @GetMapping("/topic/{id}/questions")
    public ApiResponse<List<Map<String, Object>>> getTopicQuestions(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        // topic 下的问题列表：复用 questionService（topicId 过滤）
        return ApiResponse.success(questionService.listQuestions(page, pageSize, null, id));
    }

    @GetMapping("/topic/hot")
    public ApiResponse<List<Map<String, Object>>> getHotTopics() {
        return ApiResponse.success(topicService.getHotTopics(10));
    }

    @PostMapping("/topic/{id}/follow")
    public ApiResponse<Map<String, Object>> followTopic(@PathVariable Long id, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ApiResponse.unauthorized("请先登录");

        // 最小实现：不做用户级关注表，避免前端报错
        Map<String, Object> resp = new HashMap<>();
        resp.put("is_followed", true);
        return ApiResponse.success(resp);
    }
}
