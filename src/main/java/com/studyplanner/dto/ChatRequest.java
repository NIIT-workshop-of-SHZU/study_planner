package com.studyplanner.dto;

import java.util.List;

public class ChatRequest {
    private List<ChatMessage> messages;
    private String model;
    private String customApiUrl;
    private String customApiKey;
    private String customModel;
    
    public static class ChatMessage {
        private String role;
        private String content;
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
    
    public List<ChatMessage> getMessages() {
        return messages;
    }
    
    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getCustomApiUrl() {
        return customApiUrl;
    }
    
    public void setCustomApiUrl(String customApiUrl) {
        this.customApiUrl = customApiUrl;
    }
    
    public String getCustomApiKey() {
        return customApiKey;
    }
    
    public void setCustomApiKey(String customApiKey) {
        this.customApiKey = customApiKey;
    }
    
    public String getCustomModel() {
        return customModel;
    }
    
    public void setCustomModel(String customModel) {
        this.customModel = customModel;
    }
}
