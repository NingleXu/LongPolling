package com.gdou.longPoll.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 用于监听配置文件的变化，并且 进行实时的更新
 *
 * @author ningle
 * @version : ConfigListener.java, v 0.1 2023/11/01 15:58 ningle
 **/


public class ConfigListener {

    private static final String WAIT_TIME_LABEL = "waiting time out";
    private static final ScheduledExecutorService httpListenerExecutorService = Executors.newScheduledThreadPool(2);

    static {
        // 默认监听 ip 和 端口
        listenerConfig("host");
        listenerConfig("port");

    }

    public static void init() {

    }


    public static void listenerConfig(String key) {
        // 监听 某个 配置的变化
        httpListenerExecutorService.scheduleAtFixedRate(() -> {
            System.out.println(new Date() + " 开始监听配置：" + key);
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet("http://localhost:8080/message/listen?listenKey="+key);
                RequestConfig requestConfig = RequestConfig.custom()
                        .setConnectTimeout(30000)
                        .setSocketTimeout(30000)
                        .build();
                httpGet.setConfig(requestConfig);

                CloseableHttpResponse response = httpClient.execute(httpGet);
                String responseBody = EntityUtils.toString(response.getEntity());
                // 配置发生改变
                if (!WAIT_TIME_LABEL.equals(responseBody)) {
                    System.out.println(new Date() + key + " 发生改变，新值：" + responseBody);
                    // 立即执行修改
                    Config.updateConfig(key, responseBody);
                } else {
                    System.out.println(new Date() + key + " 未发生改变");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 500, 200, TimeUnit.MILLISECONDS);

    }
}
