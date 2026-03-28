package com.example.backend.controller;


import com.example.backend.pojo.TalkMsg;
import com.example.backend.service.TalkMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/talkMsg")
public class TalkMsgController {

    @Autowired
    TalkMsgService talkMsgService;

    @PostMapping("/findMyMsg")
    public List<TalkMsg> findMyMsg(@RequestParam("sendMan") Long sendMan, @RequestParam("getMan") Long getMan){
        return talkMsgService.findMyMsg(sendMan,getMan);
    }

    @PostMapping("/findMyNoRead")
    public Integer findMyNoRead(@RequestParam("sendMan") Long sendMan,@RequestParam(value = "getMan") Long getMan){
        return talkMsgService.findMyNoRead(sendMan,getMan);
    }

    @PostMapping("/readMsg")
    public Integer readMsg(@RequestParam("sendMan") Long sendMan,@RequestParam("getMan") Long getMan){
        return talkMsgService.readMsg(sendMan,getMan);
    }

}
