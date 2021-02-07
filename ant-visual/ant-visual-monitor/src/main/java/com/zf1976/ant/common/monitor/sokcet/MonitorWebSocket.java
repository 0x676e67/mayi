package com.zf1976.ant.common.monitor.sokcet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zf1976.ant.common.monitor.pojo.SystemInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author mac
 * @date 2021/1/23
 **/
@ServerEndpoint("/api/monitor")
@Component
@Slf4j
public class MonitorWebSocket {

    private static final Map<String, Session> CLIENT_SESSION = new ConcurrentHashMap<>();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
            new ThreadFactoryBuilder().setNameFormat("monitor websocket thread").build());


    static {
        // 每秒进行群发一次
        scheduledExecutorService.scheduleWithFixedDelay(MonitorWebSocket::sendSystemInfo,
                0 ,
                1,
                TimeUnit.SECONDS);
    }

    /**
     * 发送消息
     */
    private static void sendSystemInfo() {
        if (CLIENT_SESSION.size() > 0) {
            sendAll(MonitorUtils.getSystemInfo());
        }
    }

    /**
     * 新建链接
     *
     * @param session session
     */
    @OnOpen
    public void onOpen(Session session) throws JsonProcessingException {
        log.info("new session id：{}", session.getId());
        CLIENT_SESSION.put(session.getId(), session);
        final String result = MAPPER.writeValueAsString(MonitorUtils.getSystemInfo());
        session.getAsyncRemote()
               .sendText(result);
    }

    /**
     * 发生错误
     *
     * @param throwable throwable
     */
    @OnError
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    /**
     * 收到客户端发来消息
     *
     * @param message 消息对象
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("websocket receives a message from the client：{}", message);
    }

    /**
     * 链接关闭
     *
     * @param session session
     */
    @OnClose
    public void onClose(Session session) {
        log.info("a user is disconnected, id：{}", session.getId());
        CLIENT_SESSION.remove(session.getId());
    }

    /**
     * 群发消息
     *
     * @param systemInfoVo 消息内容
     */
    private static void sendAll(SystemInfoVo systemInfoVo) {
        for (Map.Entry<String, Session> entry : CLIENT_SESSION.entrySet()) {
            try {
                final String result = MAPPER.writeValueAsString(systemInfoVo);
                entry.getValue().getAsyncRemote().sendText(result);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e.getCause());
            }
        }
    }

}
