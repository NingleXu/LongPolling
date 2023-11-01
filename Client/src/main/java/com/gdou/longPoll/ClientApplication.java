package com.gdou.longPoll;

import com.gdou.longPoll.config.ConfigListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ningle
 * @version : ClientApplication.java, v 0.1 2023/11/01 15:57 ningle
 **/
@SpringBootApplication
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication
                .run(ClientApplication.class, args)
                .start();
        ConfigListener.init();
    }
}
