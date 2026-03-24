package com.example.backend.service.impl;


import com.example.backend.dao.TalkMsgDao;
import com.example.backend.pojo.TalkMsg;
import com.example.backend.service.TalkMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TalkMsgServiceImpl implements TalkMsgService {

    @Autowired
    TalkMsgDao talkMsgDao;
    @Override
    public Integer addTalkMsg(TalkMsg talkMsg){
        return talkMsgDao.addTalkMsg(talkMsg);
    }
    @Override
    public List<TalkMsg> findMyMsg(Long sendMan,Long getMan){
        return talkMsgDao.findMyMsg(sendMan,getMan);
    }
    @Override
    public Integer findMyNoRead(Long sendMan,Long getMan){
        return talkMsgDao.findMyNoRead(sendMan,getMan);
    }
    @Override
    public Integer readMsg(Long sendMan,Long getMan){
        return talkMsgDao.readMsg(sendMan,getMan);
    }
}
