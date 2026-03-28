package com.example.backend.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@TableName(value="talk_msg")
public class TalkMsg {
    private Long id;
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private Date timestamp;
    private Long isRead;//已读状态，为0就是未读，为1就是已读
    // 构造方法、Getters 和 Setters
}