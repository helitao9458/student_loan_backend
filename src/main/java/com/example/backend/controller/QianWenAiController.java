package com.example.backend.controller;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.utils.JsonUtils;
import com.example.backend.pojo.ChatClientEntity;
import com.example.backend.tools.QianWenReplyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;

@RestController
@Slf4j
@CrossOrigin
public class QianWenAiController {

    /**
     * 客户端sse连接池
     */
    public static Map<String, SseEmitter> sseEmitterMap = new HashMap<>();

    /**
     * 开启sse连接
     */
    @GetMapping("/open")
    public SseEmitter startSse(@RequestParam String uuid) {
        // 判断sse连接池中是否存在该连接
        SseEmitter sseEmitter = sseEmitterMap.get(uuid);
        if (Objects.isNull(sseEmitter)) {
            sseEmitter = new SseEmitter(0L);
            sseEmitter.onCompletion(() -> sseEmitterMap.remove(uuid));
            sseEmitter.onTimeout(() -> sseEmitterMap.remove(uuid));
            sseEmitterMap.put(uuid, sseEmitter);
        }
        log.info("客户端{}开启了sse连接", uuid);
        return sseEmitter;
    }

    /**
     * 关闭sse连接
     */
    @GetMapping("/close")
    public void closeSse(@RequestBody String uuid) {
        // 判断sse连接池中是否存在该连接
        SseEmitter sseEmitter = sseEmitterMap.get(uuid);
        if (Objects.nonNull(sseEmitter)) {
            sseEmitter.complete();
            sseEmitterMap.remove(uuid);
        }
    }

    /**
     * 推送消息，调用大模型流式回答接口，将返回的消息推送
     */
    @PostMapping("/push")
    public void pushMessage(@RequestBody ChatClientEntity chatClientEntity, HttpServletResponse response) {
        System.out.println(chatClientEntity);
        // 流式事件流响应
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        // 判断sse连接池中是否存在该连接
        SseEmitter sseEmitter = sseEmitterMap.get(chatClientEntity.getUuid());
        Generation gen = null;
        //异步回答
        if (Objects.nonNull(sseEmitter)) {
            try {
                // 从生成式连接池中获取一个连接
                gen = QianWenReplyUtil.getGenerationPool().borrowObject();
                Semaphore semaphore = new Semaphore(0);
                StringBuilder fullContent = new StringBuilder();
                // 创建生成参数
                GenerationParam param = QianWenReplyUtil.createGenerationParam(QianWenReplyUtil.createMessage(chatClientEntity.getMessages()));
                // 调用大模型
                gen.streamCall(param, new ResultCallback<GenerationResult>() {
                    @Override
                    public void onEvent(GenerationResult message) {
                        fullContent.append(message.getOutput().getChoices().get(0).getMessage().getContent());
                        log.info("生成信息：{}", JsonUtils.toJson(message));
                        // 推送消息
                        // 生成式文本敏感词过滤（此处代码省略）
                        try {
                            sseEmitter.send(JsonUtils.toJson(message));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    @Override
                    public void onError(Exception err) {
                        log.error("发生错误{}", err.getMessage());
                        try {
                            sseEmitter.send("服务器正在升级中，请稍后再试！");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        semaphore.release();
                    }
                    @Override
                    public void onComplete() {
                        log.info("回答完成");
                        semaphore.release();
                    }
                });
                semaphore.acquire();
                log.info("Full content: \n{}", fullContent);
            }catch (Exception e){
                log.error("发生错误{}", e.getMessage());
            }finally {
                QianWenReplyUtil.getGenerationPool().returnObject(gen);
            }
        }
    }
}

