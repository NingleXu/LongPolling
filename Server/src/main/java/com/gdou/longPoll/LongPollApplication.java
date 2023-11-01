package com.gdou.longPoll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LongPollApplication {
    public static void main(String[] args) {
        SpringApplication.run(LongPollApplication.class,args)
                .start();
    }
}
