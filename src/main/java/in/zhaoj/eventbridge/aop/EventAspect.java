package in.zhaoj.eventbridge.aop;

import in.zhaoj.eventbridge.pojo.EventsFlow;
import in.zhaoj.eventbridge.websocket.ConsumerWebsocket;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
    private ConsumerWebsocket consumerWebsocket;

    @Autowired
    private EventsFlow eventsFlow;

    @Pointcut("execution(* in.zhaoj.eventbridge.pojo.EventsFlow.productEvent(..))")
    public void productEvent(){}

    @AfterReturning("productEvent()")
    public void afterProductEvent(JoinPoint joinPoint) throws IOException {
        Object[] obj = joinPoint.getArgs();
        for (Object argItem : obj) {
            if (argItem instanceof String) {
                String consumer_uuid = (String) argItem;
                if(this.consumerWebsocket.isConsumerOnilne(consumer_uuid)) {
                    //传送事件
                    this.consumerWebsocket.sendEvent(consumer_uuid, this.eventsFlow.consumeEvent(consumer_uuid));
                }
            }
        }

    }
}
