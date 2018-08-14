package in.zhaoj.eventbridge.service;

import in.zhaoj.eventbridge.pojo.Event;
import in.zhaoj.eventbridge.pojo.EventsFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

/**
 * @author: jinzhao
 * @date:2018/8/14
 * @description:
 */
public interface EventService {

    public void producerProductEvent(String consumer_uuid, Event event);

    public Event consumerConsumeEvent(String consumer_uuid);
}
