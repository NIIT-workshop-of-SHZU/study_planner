package com.studyplanner.service;

import com.studyplanner.dto.ChatMessageResponse;
import com.studyplanner.entity.ChatMessage;
import com.studyplanner.entity.User;
import com.studyplanner.mapper.ChatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 聊天服务类
 */
@Service
public class ChatService {
    
    private static final Logger logger = Logger.getLogger(ChatService.class.getName());
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ChatMessageMapper chatMessageMapper;
    
    // 存储在线用户（userId -> UserInfo）
    private final Map<Long, ChatMessageResponse.UserInfo> onlineUsers = new ConcurrentHashMap<>();
    
    /**
     * 用户加入聊天室
     */
    public void userJoined(Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            logger.warning("用户不存在: userId=" + userId);
            return;
        }
        
        ChatMessageResponse.UserInfo userInfo = new ChatMessageResponse.UserInfo(
            user.getId(),
            user.getUsername()
        );
        
        onlineUsers.put(userId, userInfo);
        logger.info("用户加入: " + user.getUsername() + " (userId=" + userId + "), 当前在线人数: " + onlineUsers.size());
        
        // 通知所有用户有新用户加入
        ChatMessageResponse.UserEventPayload payload = new ChatMessageResponse.UserEventPayload(
            user.getId(),
            user.getUsername(),
            new ArrayList<>(onlineUsers.values())
        );
        
        ChatMessageResponse response = new ChatMessageResponse(
            "user_joined",
            payload
        );
        
        messagingTemplate.convertAndSend("/topic/chat", response);
        logger.info("已广播用户加入消息: " + user.getUsername());
    }
    
    /**
     * 用户离开聊天室
     */
    public void userLeft(Long userId) {
        ChatMessageResponse.UserInfo userInfo = onlineUsers.remove(userId);
        if (userInfo == null) {
            return;
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            return;
        }
        
        // 通知所有用户有用户离开
        ChatMessageResponse.UserEventPayload payload = new ChatMessageResponse.UserEventPayload(
            user.getId(),
            user.getUsername(),
            new ArrayList<>(onlineUsers.values())
        );
        
        ChatMessageResponse response = new ChatMessageResponse(
            "user_left",
            payload
        );
        
        messagingTemplate.convertAndSend("/topic/chat", response);
    }
    
    /**
     * 发送消息
     */
    public void sendMessage(Long userId, String content) {
        User user = userService.getUserById(userId);
        if (user == null) {
            logger.warning("发送消息失败：用户不存在: userId=" + userId);
            return;
        }
        
        // 保存消息到数据库
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(user.getId());
        chatMessage.setUsername(user.getUsername());
        chatMessage.setAvatar(user.getAvatar());
        chatMessage.setContent(content);
        chatMessage.setCreatedAt(LocalDateTime.now());
        
        try {
            chatMessageMapper.insert(chatMessage);
            logger.info("消息已保存到数据库: id=" + chatMessage.getId());
        } catch (Exception e) {
            logger.severe("保存消息到数据库失败: " + e.getMessage());
            // 即使保存失败，也继续广播消息
        }
        
        // 创建消息响应
        ChatMessageResponse.MessagePayload payload = new ChatMessageResponse.MessagePayload(
            chatMessage.getId(),
            user.getId(),
            user.getUsername(),
            user.getAvatar(),
            content,
            chatMessage.getCreatedAt()
        );
        
        ChatMessageResponse response = new ChatMessageResponse(
            "message",
            payload
        );
        
        // 广播消息给所有在线用户
        messagingTemplate.convertAndSend("/topic/chat", response);
        logger.info("已广播消息: " + user.getUsername() + ": " + content + " (在线人数: " + onlineUsers.size() + ")");
    }
    
    /**
     * 获取历史消息
     */
    public List<ChatMessageResponse.MessagePayload> getHistoryMessages(LocalDateTime before, int limit) {
        try {
            List<ChatMessage> messages = chatMessageMapper.findRecentMessages(before, limit);
            List<ChatMessageResponse.MessagePayload> result = new ArrayList<>();
            
            // 反转列表，使其按时间正序排列
            Collections.reverse(messages);
            
            for (ChatMessage msg : messages) {
                ChatMessageResponse.MessagePayload payload = new ChatMessageResponse.MessagePayload(
                    msg.getId(),
                    msg.getUserId(),
                    msg.getUsername(),
                    msg.getAvatar(),
                    msg.getContent(),
                    msg.getCreatedAt()
                );
                result.add(payload);
            }
            
            logger.info("加载历史消息: " + result.size() + " 条");
            return result;
        } catch (Exception e) {
            logger.severe("加载历史消息失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取在线用户列表
     */
    public List<ChatMessageResponse.UserInfo> getOnlineUsers() {
        return new ArrayList<>(onlineUsers.values());
    }
    
    /**
     * 发送在线用户列表给指定用户
     */
    public void sendOnlineUsers(Long userId) {
        List<ChatMessageResponse.UserInfo> users = getOnlineUsers();
        ChatMessageResponse response = new ChatMessageResponse(
            "online_users",
            users
        );
        
        messagingTemplate.convertAndSendToUser(
            String.valueOf(userId),
            "/queue/chat",
            response
        );
    }
    
    /**
     * 广播在线用户列表给所有订阅者
     */
    public void broadcastOnlineUsers() {
        List<ChatMessageResponse.UserInfo> users = getOnlineUsers();
        ChatMessageResponse response = new ChatMessageResponse(
            "online_users",
            users
        );
        
        // 广播给所有订阅 /topic/chat 的用户
        messagingTemplate.convertAndSend("/topic/chat", response);
        logger.info("已广播在线用户列表，当前在线人数: " + users.size());
    }
    
    /**
     * 发送错误消息
     */
    public void sendError(Long userId, String message) {
        ChatMessageResponse.ErrorPayload payload = new ChatMessageResponse.ErrorPayload(message);
        ChatMessageResponse response = new ChatMessageResponse("error", payload);
        
        messagingTemplate.convertAndSendToUser(
            String.valueOf(userId),
            "/queue/chat",
            response
        );
    }
    
    /**
     * 检查用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        return onlineUsers.containsKey(userId);
    }
    
    /**
     * 获取消息总数
     */
    public long getTotalMessageCount() {
        try {
            return chatMessageMapper.count();
        } catch (Exception e) {
            logger.severe("获取消息总数失败: " + e.getMessage());
            return 0;
        }
    }
}

