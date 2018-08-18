package in.zhaoj.eventbridge.websocket;

import in.zhaoj.eventbridge.pojo.ConsumerSocketSessionWarehouse;
import in.zhaoj.eventbridge.pojo.Event;
import in.zhaoj.eventbridge.pojo.EventsFlow;
import in.zhaoj.eventbridge.pojo.Response;
import in.zhaoj.eventbridge.util.ConsumerWebSocketSessionUtil;
import in.zhaoj.eventbridge.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author: jinzhao
 * @date:2018/8/19
 * @description: 消费者 websocket
 */
@Component
public class ConsumerWebsocketHandler extends TextWebSocketHandler
{
    @Autowired
    private ConsumerSocketSessionWarehouse consumerSocketSessionWarehouse;

    @Autowired
    private ConsumerWebSocketSessionUtil consumerWebSocketSessionUtil;

    @Autowired
    private EventsFlow eventsFlow;

    @Value("${system.consumer_key}")
    private String consumer_key;

    private final Logger logger = LoggerFactory.getLogger(ConsumerWebsocketHandler.class);

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception
    {
        super.afterConnectionClosed(session, status);

        String uuid = consumerSocketSessionWarehouse.deleteConsumerByWebSocket(session);
        if(uuid != null) {
            logger.info("消费者客户端下线：" + uuid);
        } else {
            logger.info("消费者客户端下线： 匿名");
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        super.afterConnectionEstablished(session);
        logger.info("消费者客户端上线 IP:" + session.getRemoteAddress().getAddress());
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
                    consumerWebSocketSessionUtil.sendResponse(session, new Response(Response.CODE_KEY_ERROR));
                    return;
                }

                String consumerUuid = (String) json.get("consumer_uuid");

                //存上
                consumerSocketSessionWarehouse.addConsumer(session, consumerUuid);

                consumerWebSocketSessionUtil.sendResponse(session, new Response(Response.CODE_SUCCESS));
                logger.info("消费者客户端上线 IP:" + session.getRemoteAddress().getAddress() + " UUID:" + consumerUuid);

                //处理离线消息
                Event event;
                while((event = this.eventsFlow.consumeEvent(consumerUuid)) != null) {
                    consumerWebSocketSessionUtil.sendEvent(session, event);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}