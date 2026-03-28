package com.example.backend.tools;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类
 */
public class JwtUtil {
    // 秘钥配置和默认过期时间
    public static final Long JWT_TTL = 60 * 60 * 1000L; // 默认一个小时
    public static final String JWT_KEY = "wrw";

    public static String getUUID() {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    // 生成不会过期的 JWT
    public static String createNonExpiringJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID(), false); // false 表示不设置过期时间
        return builder.compact();
    }

    // 重构 getJwtBuilder 方法，增加一个参数控制是否设置过期时间
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid, boolean setExpiration) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        JwtBuilder builder = Jwts.builder()
                .setId(uuid)
                .setSubject(subject)
                .setIssuer("sg")
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, secretKey);

        if (setExpiration && ttlMillis != null) {
            long expMillis = nowMillis + ttlMillis;
            Date expDate = new Date(expMillis);
            builder.setExpiration(expDate);
        }

        return builder;
    }

    // 为兼容原方法增加重载版本
    public static String createJWT(String subject, Long ttlMillis) {
        return getJwtBuilder(subject, ttlMillis, getUUID(), true).compact(); // 默认设置过期时间
    }

    public static String createJWT(String subject) {
        return getJwtBuilder(subject, JWT_TTL, getUUID(), true).compact(); // 默认设置过期时间
    }

    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JWT_KEY);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }
}

