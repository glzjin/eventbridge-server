package in.zhaoj.eventbridge.websocket;

import in.zhaoj.eventbridge.pojo.Event;
import in.zhaoj.eventbridge.pojo.EventsFlow;
import in.zhaoj.eventbridge.pojo.Response;
import in.zhaoj.eventbridge.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author: jinzhao
 * @date:2018/8/15
 * @description: 消费者 websocket
 */
@Component
public class ConsumerWebsocket extends TextWebSocketHandler
{
    private static ConcurrentHashMap<String, ConsumerWebsocket> webSocketMap = new ConcurrentHashMap<String, ConsumerWebsocket>();

    //与某个客户端的连接会话
    private WebSocketSession session;

    // 消费者 UUID
    private String consumer_uuid;

    @Autowired
    private EventsFlow eventsFlow;

    @Value("${system.consumer_key}")
    private String consumer_key;

    private static final Logger logger = LoggerFactory.getLogger(ConsumerWebsocket.class);

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception
    {
        super.afterConnectionClosed(session, status);

        this.closeAndRemoveWebsocket();
        if(this.consumer_uuid != null) {
            logger.info("消费者客户端下线：" + this.consumer_uuid);
        } else {
            logger.info("消费者客户端下线： 匿名");
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        super.afterConnectionEstablished(session);
        this.session = session;
        logger.info("消费者客户端上线 IP:" + session.getRemoteAddress().getAddress());
        this.autoClean();
    }

    /**
     * @api {get} /consumer/websocket 消费者Websocket
     * @apiName ConsumerWebsocket
     * @apiGroup consumeGroup
     * @apiVersion 2.0.0
     * @apiDescription Websocket  接口
     *
     * @apiParam {json} action 动作, reg
     * @apiParam {json} consumer_uuid 消费者 UUID
     * @apiParam {json} key 消费者 Key
     *
     * @apiParamExample {json} 请求示例
     * {
     *      "action": "reg",
     *      "consumer_uuid": "test",
     *      "key": "your_key"
     * }
     *
     * @apiSuccess (请求成功) {json} code 结果码
     * @apiSuccess (请求成功) {json} data 数据
     * @apiSuccessExample 请求成功
     * {
     *      code: 100,
     *         "data": {
     *           "event": {
     *               "event_id": 1
     *           }
     *     }
     * }
     *
     *  @apiError (请求失败) {json} code 错误码
     *  @apiErrorExample {json} Key 错误
     *  {
     *      code: 201
     *  }
     *
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception
    {
        super.handleTextMessage(session, message);
        String message_text = message.getPayload();
        HashMap<String, Object> json = null;
        try {
            json = JSONUtil.decode(message_text);
            String action = (String) json.get("action");
            if(action.equals("reg")) {
                logger.info("消费者客户端 注册请求 IP:" + session.getRemoteAddress().getAddress());
                //验证 Key
                if(!consumer_key.equals((String) json.get("key"))) {
                    this.sendMessage(JSONUtil.encode(new Response(Response.CODE_KEY_ERROR)));
                    return;
                }

                String consumer_uuid = (String) json.get("consumer_uuid");
                this.setWebSocketMap(consumer_uuid);

                this.sendMessage(JSONUtil.encode(new Response(Response.CODE_SUCCESS)));
                logger.info("消费者客户端上线 IP:" + session.getRemoteAddress().getAddress() + " UUID:" + this.consumer_uuid);

                //处理离线消息
                Event event;
                while((event = this.eventsFlow.consumeEvent(consumer_uuid)) != null) {
                    this.sendEvent(consumer_uuid, event);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setWebSocketMap(String consumer_uuid) throws IOException {
        ConsumerWebsocket exist_websocket = this.getWebSocketMap(consumer_uuid);
        if(exist_websocket != null) {
            exist_websocket.closeAndRemoveWebsocket();
        }

        this.consumer_uuid = consumer_uuid;
        webSocketMap.put(this.consumer_uuid, this);
    }

    public ConsumerWebsocket getWebSocketMap(String consumer_uuid) {
        ConsumerWebsocket exist_websocket = webSocketMap.get(consumer_uuid);
        return exist_websocket;
    }

    public void closeAndRemoveWebsocket() throws IOException {
        if(this.session.isOpen()) {
            this.session.close();
        }

        if(this.consumer_uuid != null) {
            webSocketMap.remove(this.consumer_uuid);
            this.consumer_uuid = null;
        }
    }

    public void sendMessage(String message) throws IOException {
        TextMessage msg = new TextMessage(message);
        this.session.sendMessage(msg);
    }

    public Boolean sendMessage(String consumer_uuid, String message) throws IOException {
        ConsumerWebsocket websocket = this.getWebSocketMap(consumer_uuid);
        if(websocket != null) {
            websocket.sendMessage(message);
            return true;
        } else {
            return false;
        }
    }

    public Boolean sendEvent(String consumer_uuid, Event event) throws IOException {
        Response response = new Response(Response.CODE_NEW_EVENT);
        response.setData("event", event);
        logger.info("推送事件请求:" + consumer_uuid + " Event ID: " + event.getEvent_id());

        ConsumerWebsocket websocket = this.getWebSocketMap(consumer_uuid);
        if(websocket != null) {
            websocket.sendMessage(JSONUtil.encode(response));
            logger.info("推送事件请求:" + consumer_uuid + " Event ID: " + event.getEvent_id() + " 成功！");
            return true;
        } else {
            return false;
        }
    }

    public Boolean isConsumerOnilne(String consumer_uuid) {
        ConsumerWebsocket websocket = this.getWebSocketMap(consumer_uuid);
        if(websocket != null) {
            return true;
        } else {
            return false;
        }
    }

    public void autoClean() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            if(this.consumer_uuid == null) {
                try {
                    this.closeAndRemoveWebsocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 10, TimeUnit.SECONDS);
    }

}