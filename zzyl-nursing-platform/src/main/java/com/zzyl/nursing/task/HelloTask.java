package com.zzyl.nursing.task;


import org.springframework.stereotype.Component;

@Component
public class HelloTask {
    public void hello() {
        System.out.println("hello task");
    }
}
