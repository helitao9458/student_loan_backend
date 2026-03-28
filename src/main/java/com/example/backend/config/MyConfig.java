package com.example.backend.config;

import com.alibaba.dashscope.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyConfig {

    private static String apiKey="sk-e7544ae643fc4f66bf11fe0459fa958e";

    @PostConstruct
    public void config(){
        //设置APIKEY
        Constants.apiKey=apiKey;
    }
}