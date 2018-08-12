package in.zhaoj.eventbridge.service;

import in.zhaoj.eventbridge.pojo.Event;
import in.zhaoj.eventbridge.pojo.EventsFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author: jinzhao
 * @date:2018/8/12
 * @description:
 */
@Controller
public class EventService {

    @Autowired
    private EventsFlow eventsFlow;

    public void producerProductEvent(Event event) {
        this.eventsFlow.productEvent(event);
    }

    public Event consumerConsumeEvent() {
        return this.eventsFlow.consumeEvent();
    }
}
