package com.gdou.longPoll.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ningle
 * @version : ConfigPrinter.java, v 0.1 2023/11/01 16:22 ningle
 **/
@Component
public class ConfigPrinter {
    private static final ScheduledExecutorService configPrinterExecutorService = Executors.newSingleThreadScheduledExecutor();

    static {
        configPrinterExecutorService.scheduleAtFixedRate(() -> {
            for (Map.Entry<String, String> stringStringEntry : Config.CONFIG.entrySet()) {
                System.out.print(stringStringEntry.getKey() + " : " + stringStringEntry.getValue());
                System.out.print(" || ");
            }
            System.out.println();
        }, 500, 800, TimeUnit.MILLISECONDS);
    }
}
