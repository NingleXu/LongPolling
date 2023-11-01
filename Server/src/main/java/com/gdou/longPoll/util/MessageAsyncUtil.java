package com.gdou.longPoll.util;

import com.gdou.longPoll.task.LongPollingTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

// 消息异步处理工具
public class MessageAsyncUtil {

    private static final Map<String, CopyOnWriteArrayList<LongPollingTask>> taskMap = new ConcurrentHashMap<>();

    // 创建异步处理线程池
    public static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);


    // 对任务进行添加
    public static void addLongPollingTask(LongPollingTask task) {
        taskMap.computeIfAbsent(task.getListenKey(), key -> new CopyOnWriteArrayList<>()).add(task);
    }

    /**
     * 这个监听的key发生了改变，对相关监听的请求进行响应
     *
     * @param key 发生改变的key
     */
    public static void doUpdateForListenServer(String key) {
        if (!taskMap.containsKey(key)) return;
        // 对它监听的task
        for (LongPollingTask longPollingTask : taskMap.get(key)) {
            longPollingTask.sendResponse(key);
        }
    }


    /**
     * 移除长轮询任务
     *
     * @param longPollingTask 要移除的长轮询任务
     */
    public static void removeLongPollingTask(LongPollingTask longPollingTask) {
        String key = longPollingTask.getListenKey(); // 获取任务的key
        CopyOnWriteArrayList<LongPollingTask> taskCopyOnWriteArrayList = taskMap.get(key); // 获取任务列表
        taskCopyOnWriteArrayList.remove(longPollingTask);
    }

}
