package in.zhaoj.eventbridge.service.impl;

import in.zhaoj.eventbridge.pojo.Event;
import in.zhaoj.eventbridge.pojo.EventsFlow;
import in.zhaoj.eventbridge.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: jinzhao
 * @date:2018/8/14
 * @description:
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventsFlow eventsFlow;

    public void producerProductEvent(String consumer_uuid, Event event) {
        this.eventsFlow.productEvent(consumer_uuid, event);
    }

    public Event consumerConsumeEvent(String consumer_uuid) {
        return this.eventsFlow.consumeEvent(consumer_uuid);
    }
}
