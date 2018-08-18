package in.zhaoj.eventbridge.util;

import in.zhaoj.eventbridge.pojo.ConsumerSocketSessionWarehouse;
import in.zhaoj.eventbridge.pojo.Event;
import in.zhaoj.eventbridge.pojo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @author: jinzhao
 * @date:2018/8/19
 * @description:
 */
@Component
public class ConsumerWebSocketSessionUtil {

    @Autowired
    private ConsumerSocketSessionWarehouse consumerSocketSessionWarehouse;

    private final Logger logger = LoggerFactory.getLogger(ConsumerWebSocketSessionUtil.class);

    public void sendResponse(WebSocketSession webSocketSession, Response response) {
        try {
            webSocketSession.sendMessage(new TextMessage(JSONUtil.encode(response)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEvent(WebSocketSession webSocketSession, Event event) {
        Response response = new Response(Response.CODE_NEW_EVENT);
        response.setData("event", event);

        String consumerUUID = consumerSocketSessionWarehouse.getUUIDByWebSocket(webSocketSession);
        logger.info("推送事件请求:" + consumerUUID + " Event ID: " + event.getEvent_id());

        this.sendResponse(webSocketSession, response);
        logger.info("推送事件请求:" + consumerUUID + " Event ID: " + event.getEvent_id() + " 成功！");
    }
}
