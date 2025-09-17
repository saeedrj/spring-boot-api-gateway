package com.gateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    @Value("${jwt.expiration}")
    private long expiration;

    private final RedisTemplate<String, Integer> integerRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    public void incrementFailedAttempts(String key) {
        try {
            if (integerRedisTemplate.opsForValue().get(key) == null) {
                integerRedisTemplate.opsForValue().set(key, 0);
            }
            integerRedisTemplate.opsForValue().increment(key);
            integerRedisTemplate.expire(key, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Error incrementing failed attempts in Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Integer getFailedAttempts(String key) {
        try {
            Integer attempts = integerRedisTemplate.opsForValue().get(key);
            return attempts != null ? attempts : 0;
        } catch (Exception e) {
            log.error("Error getting failed attempts from Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void resetFailedAttempts(String key) {
        try {
            integerRedisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Error resetting failed attempts in Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void setDeviceIdForJti(String jti, String deviceId) {
        try {
            stringRedisTemplate.opsForValue().set(jti, deviceId);
            stringRedisTemplate.expire(jti, expiration, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Error setting device ID for jti in Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void setIdLogin(String jti, String userId) {
        try {
            stringRedisTemplate.opsForValue().set(jti, userId);
            stringRedisTemplate.expire(jti, expiration, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("Error setIdLogin  in Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getIdLogin(String jti) {
        try {
            return stringRedisTemplate.opsForValue().get(jti);
        } catch (Exception e) {
            if (e instanceof NullPointerException) return null;
            log.error("Error getting IdLogin for jti from Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void deleteIdLogin(String jti) {  //TODO when change permission user should delete id
        try {
            stringRedisTemplate.delete(jti);
        } catch (Exception e) {
            log.error("Error deleting IdLogin for jti in Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public boolean forgetPasswordAttempts(String key) {
        try {
            if (stringRedisTemplate.opsForValue().get(key) == null) {
                stringRedisTemplate.opsForValue().set(key, "Locked ResetPassword");
                stringRedisTemplate.expire(key, 2, TimeUnit.MINUTES);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Error incrementing ForgetPasswordAttempts in Redis: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
