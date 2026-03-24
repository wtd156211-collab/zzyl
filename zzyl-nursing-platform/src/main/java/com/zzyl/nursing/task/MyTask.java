package com.zzyl.nursing.task;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyTask {
    /**
     * 定时任务
     */
//    @Scheduled(cron = "0/5 * * * * ?")
    public void execute(){
        log.info("执行定时任务开始:{}", LocalDateTime.now());
    }
}
