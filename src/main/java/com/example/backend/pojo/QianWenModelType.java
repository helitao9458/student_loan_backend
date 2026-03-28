package com.example.backend.pojo;

import lombok.Getter;

@Getter
public enum QianWenModelType {

    /**
     * 模型名和描述
     */
    QWEN_TOURBO("qwen-turbo", "千问Touber"),
    QWEN_PLUS("qwen-plus", "千问Plus");

    private final String modelName;
    private final String modelDescription;

    QianWenModelType(String modelName, String modelDescription) {
        this.modelName = modelName;
        this.modelDescription = modelDescription;
    }
}