package com.example.backend.dao;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.pojo.TalkMsg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TalkMsgDao extends BaseMapper<TalkMsg> {
    Integer addTalkMsg(TalkMsg talkMsg);
    List<TalkMsg> findMyMsg(@Param("sendMan")Long sendMan, @Param("getMan")Long getMan);
    Integer findMyNoRead(@Param("sendMan")Long sendMan, @Param("getMan")Long getMan);
    Integer readMsg(@Param("sendMan") Long sendMan,@Param("getMan") Long getMan);
}
