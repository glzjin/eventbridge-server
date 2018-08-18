package in.zhaoj.eventbridge.pojo;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: jinzhao
 * @date:2018/8/19
 * @description:
 */
@Component
public class ConsumerSocketSessionWarehouse {
    private ConcurrentHashMap<String, WebSocketSession> uuidSessionMap = new ConcurrentHashMap<String, WebSocketSession>();

    private ConcurrentHashMap<String, String> sessionIdUuidMap = new ConcurrentHashMap<String, String>();

    public void addConsumer(WebSocketSession webSocketSession, String comsumerUUID) {
        // 清退已有的
        WebSocketSession existWebSocketSession = this.getWebSocketByUUID(comsumerUUID);
        if(existWebSocketSession != null) {
            if(existWebSocketSession.isOpen()) {
                try {
                    existWebSocketSession.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.deleteConsumerByWebSocket(existWebSocketSession);
        }

        this.uuidSessionMap.put(comsumerUUID, webSocketSession);
        this.sessionIdUuidMap.put(webSocketSession.getId(), comsumerUUID);
    }

    public String deleteConsumerByWebSocket(WebSocketSession webSocketSession) {
        String sessionId = webSocketSession.getId();
        String comsumerUUID = this.sessionIdUuidMap.get(sessionId);
        if(comsumerUUID != null) {
            this.uuidSessionMap.remove(comsumerUUID);
            this.sessionIdUuidMap.remove(sessionId);
        }

        return comsumerUUID;
    }

    public WebSocketSession getWebSocketByUUID(String comsumerUUID) {
        return this.uuidSessionMap.get(comsumerUUID);
    }

    public String getUUIDByWebSocket(WebSocketSession webSocketSession) {
        return this.sessionIdUuidMap.get(webSocketSession.getId());
    }
}
