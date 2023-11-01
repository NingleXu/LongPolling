package com.gdou.longPoll.task;

import com.gdou.longPoll.config.Config;
import com.gdou.longPoll.util.MessageAsyncUtil;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

public class LongPollingTask implements Runnable {

    // 感兴趣的 key
    private String listenKey;

    private AsyncContext asyncContext;

    private ScheduledFuture<?> scheduledFuture;

    @Override
    public void run() {
        scheduledFuture = MessageAsyncUtil.dealAsyncMessage(asyncContext);
        // 将自己加入其中，以便被寻找
        MessageAsyncUtil.addLongPollingTask(LongPollingTask.this);
    }

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
            asyncContext.complete();
        }
    }

    public LongPollingTask(String listenKey, AsyncContext asyncContext) {
        this.listenKey = listenKey;
        this.asyncContext = asyncContext;
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
