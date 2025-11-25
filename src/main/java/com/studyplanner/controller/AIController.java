package com.studyplanner.controller;

import com.studyplanner.dto.ApiResponse;
import com.studyplanner.dto.ChatRequest;
import com.studyplanner.service.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    
    @Autowired
    private LLMService llmService;
    
    @Value("${llm.api.base-url}")
    private String systemApiUrl;
    
    @Value("${llm.api.api-key}")
    private String systemApiKey;
    
    @Value("${llm.api.model:Qwen/Qwen3-8B}")
    private String defaultModel;
    
    /**
     * 登录用户使用系统API进行聊天
     */
    @PostMapping("/chat")
    public ApiResponse<String> chat(@RequestBody ChatRequest request) {
        try {
            String model = request.getModel();
            if (model == null || model.isEmpty()) {
                model = defaultModel;
            }
            
            // 转换消息格式
            List<Map<String, String>> messages = convertMessages(request.getMessages());
            
            // 调用系统API
            String response = llmService.chat(systemApiUrl, systemApiKey, model, messages);
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("AI服务调用失败: " + e.getMessage());
        }
    }
    
    /**
     * 未登录用户使用自定义API进行聊天
     */
    @PostMapping("/chat/public")
    public ApiResponse<String> chatPublic(@RequestBody ChatRequest request) {
        try {
            // 验证必要参数
            if (request.getCustomApiUrl() == null || request.getCustomApiUrl().isEmpty()) {
                return ApiResponse.error("请提供API URL");
            }
            if (request.getCustomApiKey() == null || request.getCustomApiKey().isEmpty()) {
                return ApiResponse.error("请提供API Key");
            }
            if (request.getCustomModel() == null || request.getCustomModel().isEmpty()) {
                return ApiResponse.error("请提供模型名称");
            }
            
            // 转换消息格式
            List<Map<String, String>> messages = convertMessages(request.getMessages());
            
            // 调用自定义API
            String response = llmService.chat(
                request.getCustomApiUrl(),
                request.getCustomApiKey(),
                request.getCustomModel(),
                messages
            );
            
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("AI服务调用失败: " + e.getMessage());
        }
    }
    
    private List<Map<String, String>> convertMessages(List<ChatRequest.ChatMessage> chatMessages) {
        if (chatMessages == null) {
            return List.of();
        }
        return chatMessages.stream()
            .map(msg -> Map.of("role", msg.getRole(), "content", msg.getContent()))
            .collect(Collectors.toList());
    }
}
