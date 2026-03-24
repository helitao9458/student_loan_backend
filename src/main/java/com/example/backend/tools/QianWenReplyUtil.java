package com.example.backend.tools;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.common.Message;
import com.example.backend.pojo.MessageEntity;
import com.example.backend.pojo.QianWenModelType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class QianWenReplyUtil {

    /**
     * 生成式文本请求连接池
     */
    private static GenericObjectPool<Generation> generationPool;


    /**
     * 设置生成参数
     * @param messages 消息
     * @return 配置参数
     */
    public static GenerationParam createGenerationParam(List<Message> messages) {
        return GenerationParam.builder()
                .model(QianWenModelType.QWEN_TOURBO.getModelName())
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .incrementalOutput(true)
                .build();
    }


    /**
     * 消息转换
     */
    public static List<Message> createMessage(List<MessageEntity> messageEntities) {
        List<Message> messages = new ArrayList<>();
        for (MessageEntity messageEntity : messageEntities) {
            Message message = Message.builder().role(messageEntity.getRole()).content(messageEntity.getContent()).build();
            messages.add(message);
        }
        return messages;
    }


    /**
     * 获取连接池（单例）
     */
    public static GenericObjectPool<Generation> getGenerationPool() {
        if (Objects.isNull(generationPool)) {
            //开启连接池
            PooledDashScopeObjectFactory pooledDashScopeObjectFactory = new PooledDashScopeObjectFactory();
            GenericObjectPoolConfig<Generation> config = new GenericObjectPoolConfig<>();
            // 对于语音服务，websocket协议，保持下面值相同
            config.setMaxTotal(32);
            config.setMaxIdle(32);
            config.setMinIdle(32);
            generationPool = new GenericObjectPool<>(pooledDashScopeObjectFactory, config);
        }
        return generationPool;
    }


    /**
     * 生成式文本敏感词过滤
     */
    public static String filterSensitiveWords(String text) {
        // TODO
        return text;
    }
}
