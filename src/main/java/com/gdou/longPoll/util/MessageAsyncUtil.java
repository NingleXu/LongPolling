package com.gdou.longPoll.util;

import com.gdou.longPoll.task.LongPollingTask;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

// 消息异步处理工具
public class MessageAsyncUtil {

    private static final Map<String, CopyOnWriteArrayList<LongPollingTask>> taskMap = new ConcurrentHashMap<>();

    // 创建异步处理线程池
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);


    // 执行异步处理
    public static ScheduledFuture<?> dealAsyncMessage(AsyncContext asyncContext) {
        return executorService.schedule(() -> {
            ServletResponse response = asyncContext.getResponse();
            // 已经超时 返回结果
            try {
                response.getWriter().write("wait time out");
                asyncContext.complete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 29500, TimeUnit.MILLISECONDS);
    }

    public static void addLongPollingTask(LongPollingTask task) {
        taskMap.computeIfAbsent(task.getListenKey(), key -> new CopyOnWriteArrayList<>()).add(task);
    }

    public static void doUpdateForListenServer(String key) {
        // 对它监听的task
        for (LongPollingTask longPollingTask : taskMap.get(key)) {
            longPollingTask.sendResponse(key);
        }
    }
}
