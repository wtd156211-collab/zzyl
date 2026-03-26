package com.zzyl;

import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.vo.NursingProjectVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;

@SpringBootTest
public class SerializerTest {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Test
    public void testObjectSerializer() {
        //准备一个对象
        NursingProject np= new NursingProject();
        np.setId(1L);
        np.setName("护理项目1");
        np.setOrderNo(1);
        np.setUnit("次");
        np.setPrice(new BigDecimal(10));
        np.setStatus(1);

        //将对象序列化保存到redis中
        redisTemplate.opsForValue().set("nursingProject", np);

        //获取对象
        NursingProject np2 = (NursingProject) redisTemplate.opsForValue().get("nursingProject");
        System.out.println(np2);
    }

    @Test
    public void testObjectSerializer2() {
        //准备一个对象
        NursingProjectVo np= new NursingProjectVo();
        np.setLabel("护理项目1");
        np.setValue("1");

        //将对象序列化保存到redis中
        redisTemplate.opsForValue().set("nursingProjectVo", np);

        //获取对象
        NursingProjectVo np2 = (NursingProjectVo) redisTemplate.opsForValue().get("nursingProjectVo");
        System.out.println(np2);
    }
}
