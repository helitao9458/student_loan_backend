package com.example.backend.service;


import com.example.backend.pojo.TalkMsg;

import java.util.List;

public interface TalkMsgService {
    Integer addTalkMsg(TalkMsg talkMsg);
    List<TalkMsg> findMyMsg(Long sendMan, Long getMan);
    Integer findMyNoRead(Long sendMan,Long getMan);
    Integer readMsg(Long sendMan,Long getMan);
}
