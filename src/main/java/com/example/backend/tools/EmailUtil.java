package com.example.backend.tools;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class EmailUtil {

    // 随机生成 8 位数字和字符（不包括数字1、0和字母O、l）
    public String generateRandomPassword() {
        String[] beforeShuffle = new String[] { 
            "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", 
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "m", "n", "p", "q", "r", "s", "t", "u", "v", 
            "w", "x", "y", "z" 
        };
        List<String> list = Arrays.asList(beforeShuffle);  // 将数组转换为列表
        Collections.shuffle(list);  // 打乱顺序
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {  // 生成8位密码
            sb.append(list.get(i));
        }
        return sb.toString();  // 返回生成的密码
    }

    // 发送邮件代码
    public static void sendAuthCodeEmail(String email, String newPassword) {
        try {
            SimpleEmail mail = new SimpleEmail();
            mail.setHostName("smtp.qq.com");  // 设置发送邮件的服务器
            mail.setAuthentication("3114681134@qq.com","euuonaelxuyudeig");  // 授权码
            mail.setFrom("3114681134@qq.com", "国家助学贷款系统");  // 发送邮箱和发件人名称
            mail.setSSLOnConnect(true);  // 使用 SSL 安全链接
            mail.addTo(email);  // 收件人邮箱
            mail.setSubject("密码重置通知");  // 邮件主题
            mail.setMsg("尊敬的用户: 您好!\n 您的新密码为: " + newPassword + "\n请尽快登录并修改您的密码。感谢您的使用！");  // 邮件内容
            mail.send();  // 发送邮件
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
    // 发送自定义邮件
    public static void sendCustomEmail(String email, String subject, String message) {
        try {
            SimpleEmail mail = new SimpleEmail();
            mail.setHostName("smtp.qq.com");  // 设置发送邮件的服务器
            mail.setAuthentication("3114681134@qq.com", "euuonaelxuyudeig");  // 授权码
            mail.setFrom("3114681134@qq.com", "国家助学贷款系统");  // 发送邮箱和发件人名称
            mail.setSSLOnConnect(true);  // 使用 SSL 安全链接
            mail.addTo(email);  // 收件人邮箱
            mail.setSubject(subject);  // 设置自定义主题
            mail.setMsg(message);  // 设置自定义内容
            mail.send();  // 发送邮件
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}
