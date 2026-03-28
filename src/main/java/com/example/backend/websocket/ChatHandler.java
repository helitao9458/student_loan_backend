package com.example.backend.websocket;

import com.example.backend.pojo.TalkMsg;
import com.example.backend.service.TalkMsgService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatHandler extends TextWebSocketHandler {

    @Autowired
    TalkMsgService talkMsgService;
    // 存储在线用户会话 (使用用户名作为 key)
    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // 存储离线消息 (使用用户名作为 key)
    private Map<String, List<TalkMsg>> offlineMessages = new ConcurrentHashMap<>();

    // 当用户建立连接时调用
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String username = query.split("=")[1];
        if (username != null) {
            sessions.put(username, session);
            session.getAttributes().put("username", username);
            System.out.println(username + " 已连接。");
            System.out.println("这里是"+offlineMessages);
            // 推送未读消息
            if (offlineMessages.containsKey(username)) {
                System.out.println("向用户"+username+"推送离线消息！");
                List<TalkMsg> messages = offlineMessages.get(username);
                // 创建一个包含 receiverUsername 和 messages 的 JSON 对象
                Map<String, Object> response = new HashMap<>();
                response.put("receiverUsername", username);
                response.put("messages", messages);

                // 使用 ObjectMapper 将 response 转换为 JSON 字符串
                String jsonResponse = new ObjectMapper().writeValueAsString(response);

                // 发送 JSON 格式的未读消息
                session.sendMessage(new TextMessage(jsonResponse));

                // 清空该用户的离线消息
//                offlineMessages.remove(username);
            }
        } else {
            System.out.println("用户ID为空，无法建立连接。");
        }
    }


    // 处理从前端发送过来的消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("接收: " + payload);

        // 使用 ObjectMapper 解析 JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(payload);

        // 提取字段
        String senderUsername = jsonNode.get("senderUsername").asText(); // 发送者
        String receiverUsername = jsonNode.get("receiverUsername").asText(); // 接收者
        String content = jsonNode.get("content").asText(); // 消息内容


        // 处理提取到的数据
        TalkMsg chatMessage = new TalkMsg();
        chatMessage.setSenderUsername(senderUsername);
        chatMessage.setReceiverUsername(receiverUsername);
        chatMessage.setContent(content);
        chatMessage.setTimestamp(new Date());
        chatMessage.setIsRead(0L); // 设置为未读
        talkMsgService.addTalkMsg(chatMessage);

        // 使用 ZonedDateTime 解析时间字符串
        chatMessage.setTimestamp(new Date()); // 设置时间
        // 发送给指定用户
        WebSocketSession receiverSession = sessions.get(receiverUsername);
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(formatMessage(chatMessage))); // 在线时直接发送消息
            System.out.println("已发送消息从 " + senderUsername + " 到 " + receiverUsername);
        } else {
            // 用户不在线时，将消息存入离线消息列表
            offlineMessages.putIfAbsent(receiverUsername, new ArrayList<>());
            offlineMessages.get(receiverUsername).add(chatMessage);
            System.out.println("2@"+offlineMessages);
            System.out.println(receiverUsername + " 不在线，消息已存入离线消息列表。");
        }
    }

    // 当连接关闭时调用
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            sessions.remove(username);
            System.out.println(username + " 已断开连接。");
        } else {
            System.out.println("无法识别的用户，连接已关闭。");
        }
    }

    // 格式化消息为字符串
    private String formatMessage(TalkMsg msg) {
        return String.format("来自 %s 的消息 [%s]: %s", msg.getSenderUsername(), msg.getTimestamp(), msg.getContent());
    }
}
