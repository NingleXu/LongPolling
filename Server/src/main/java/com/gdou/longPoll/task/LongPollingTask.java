package com.gdou.longPoll.task;

import com.gdou.longPoll.config.Config;
import com.gdou.longPoll.util.MessageAsyncUtil;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.gdou.longPoll.util.MessageAsyncUtil.executorService;
import static com.gdou.longPoll.util.MessageAsyncUtil.removeLongPollingTask;

public class LongPollingTask implements Runnable {

    // 感兴趣的 key
    private String listenKey;

    private String identity;

    private AsyncContext asyncContext;

    private ScheduledFuture<?> scheduledFuture;

    @Override
    public void run() {
        // 超时处理 ，如果超时说明消息是没有被改变的
        scheduledFuture = dealAyscnTimeOut();
        // 将自己加入异步请求中
        MessageAsyncUtil.addLongPollingTask(LongPollingTask.this);
    }

    /**
     * 添加自动的超时处理
     *
     * @return
     */
    public ScheduledFuture<?> dealAyscnTimeOut() {
        return executorService.schedule(() -> {
            // 已经超时 返回结果
            try {
                ServletResponse response = asyncContext.getResponse();
                response.getWriter().write("waiting time out");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                asyncContext.complete();
                // 任务结束就删除
                removeLongPollingTask(this);
            }
        }, 29500, TimeUnit.MILLISECONDS);
    }

    /**
     * 监听到key发生改变后 立即响应改变后的结果
     *
     * @param key 发生改变的key
     */
    public void sendResponse(String key) {
        if (null != scheduledFuture) {
            scheduledFuture.cancel(false);
        }
        HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
        try {
            response.setHeader("Prama", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-cache,no-store");
            response.setStatus(HttpServletResponse.SC_OK);
            // 写入更新后的值
            response.getWriter().write(Config.CONFIG.get(key));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 从列表中移除
            removeLongPollingTask(LongPollingTask.this);
            asyncContext.complete();
        }
    }

    public LongPollingTask(String listenKey, AsyncContext asyncContext) {
        this.listenKey = listenKey;
        this.asyncContext = asyncContext;
        this.identity = asyncContext.getRequest().getLocalAddr() + ":"
                + asyncContext.getRequest().getLocalPort() + ":"
                + listenKey;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getListenKey() {
        return listenKey;
    }

    public void setListenKey(String listenKey) {
        this.listenKey = listenKey;
    }

    public AsyncContext getAsyncContext() {
        return asyncContext;
    }

    public void setAsyncContext(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}
