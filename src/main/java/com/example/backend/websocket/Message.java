package com.example.backend.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private LocalDateTime timestamp;

    // 构造方法、Getters 和 Setters
}
