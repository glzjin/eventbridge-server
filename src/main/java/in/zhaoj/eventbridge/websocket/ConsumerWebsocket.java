package in.zhaoj.eventbridge.websocket;

import in.zhaoj.eventbridge.pojo.Event;
import in.zhaoj.eventbridge.pojo.EventsFlow;
import in.zhaoj.eventbridge.pojo.Response;
import in.zhaoj.eventbridge.util.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

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

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception
    {
        super.afterConnectionClosed(session, status);
        if(this.consumer_uuid != null) {
            webSocketMap.remove(this.consumer_uuid);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        super.afterConnectionEstablished(session);
        this.session = session;
    }

    /**
     * @api {get} /consumer/websocket 消费者Websocket
     * @apiName ConsumerWebsocket
     * @apiGroup consumeGroup
     * @apiVersion 2.0.0
     * @apiDescription Websocket  接口
     *
     * @apiParam {json} action 动作, reg or ping
     * @apiParam {json} consumer_uuid 消费者 UUID
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
        HashMap<String, Object> json = null;
        try {
            json = JSONUtil.decode(message.getPayload());

            if(json.get("action").equals("reg")) {
                //验证 Key
                if(!consumer_key.equals((String) json.get("key"))) {
                    this.sendMessage(JSONUtil.encode(new Response(Response.CODE_KEY_ERROR)));
                    return;
                }

                this.consumer_uuid = (String) json.get("consumer_uuid");
                webSocketMap.put(this.consumer_uuid, this);

                this.sendMessage(JSONUtil.encode(new Response(Response.CODE_SUCCESS)));

                //处理离线消息
                Event event;
                while((event = this.eventsFlow.consumeEvent(consumer_uuid)) != null) {
                    this.sendEvent(consumer_uuid, event);
                }
            }

            if(json.get("action").equals("ping")) {
                this.sendMessage(JSONUtil.encode(new Response(Response.CODE_SUCCESS_PONG)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String message) throws IOException {
        TextMessage msg = new TextMessage(message);
        this.session.sendMessage(msg);
    }

    public Boolean sendMessage(String consumer_uuid, String message) throws IOException {
        ConsumerWebsocket websocket = this.webSocketMap.get(consumer_uuid);
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

        ConsumerWebsocket websocket = this.webSocketMap.get(consumer_uuid);
        if(websocket != null) {
            websocket.sendMessage(JSONUtil.encode(response));
            return true;
        } else {
            return false;
        }
    }

    public Boolean isConsumerOnilne(String consumer_uuid) {
        ConsumerWebsocket websocket = this.webSocketMap.get(consumer_uuid);
        if(websocket != null) {
            return true;
        } else {
            return false;
        }
    }

}