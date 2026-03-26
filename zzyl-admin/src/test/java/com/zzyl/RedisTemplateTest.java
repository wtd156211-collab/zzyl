package com.zzyl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void test() {
        System.out.println(redisTemplate);
    }
}
