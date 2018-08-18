package in.zhaoj.eventbridge.aop;

import in.zhaoj.eventbridge.pojo.ConsumerSocketSessionWarehouse;
import in.zhaoj.eventbridge.pojo.EventsFlow;
import in.zhaoj.eventbridge.util.ConsumerWebSocketSessionUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @author: jinzhao
 * @date:2018/8/9
 * @description:给控制器传 Extstars ID
 */
@Aspect
@Component
@Order(1)
public class EventAspect {

    @Autowired
    private ConsumerSocketSessionWarehouse consumerSocketSessionWarehouse;

    @Autowired
    private ConsumerWebSocketSessionUtil consumerWebSocketSessionUtil;

    @Autowired
    private EventsFlow eventsFlow;

    @Pointcut("execution(* in.zhaoj.eventbridge.pojo.EventsFlow.productEvent(..))")
    public void productEvent(){}

    @AfterReturning("productEvent()")
    public void afterProductEvent(JoinPoint joinPoint) throws IOException {
        Object[] obj = joinPoint.getArgs();
        for (Object argItem : obj) {
            if (argItem instanceof String) {
                String consumerUUID = (String) argItem;
                WebSocketSession webSocket = this.consumerSocketSessionWarehouse.getWebSocketByUUID(consumerUUID);
                if(webSocket != null) {
                    //传送事件
                    this.consumerWebSocketSessionUtil.sendEvent(webSocket, this.eventsFlow.consumeEvent(consumerUUID));
                }
            }
        }

    }
}
