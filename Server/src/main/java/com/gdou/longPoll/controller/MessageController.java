package com.gdou.longPoll.controller;

import com.gdou.longPoll.config.Config;
import com.gdou.longPoll.task.LongPollingTask;
import com.gdou.longPoll.util.MessageAsyncUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/message")
public class MessageController {

    // 消息监听
    @GetMapping("/listen")
    public void messageListen(@RequestParam("listenKey") String listenKey, HttpServletResponse response, HttpServletRequest request) {
        System.out.println("接受到对[" + listenKey + "]的监听请求");
        // 异步处理上下文
        AsyncContext asyncContext = request.startAsync(request, response);
        // 构建任务 以及 异步超时处理
        asyncContext.start(new LongPollingTask(listenKey, asyncContext));
    }

    @PostMapping("/modify")
    public void modifyKey(@RequestParam("key") String key, @RequestParam("val") String val) {
        // 对配置进行修改
        if (!Config.CONFIG.containsKey(key)) return;
        // 对配置进行修改
        Config.CONFIG.put(key, val);
        // 让监听该配置的 服务进行 修改
        MessageAsyncUtil.doUpdateForListenServer(key);
    }
}
