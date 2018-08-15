package in.zhaoj.eventbridge.service;

import in.zhaoj.eventbridge.pojo.Event;

/**
 * @author: jinzhao
 * @date:2018/8/14
 * @description:
 */
public interface EventService {

    public void producerProductEvent(String consumer_uuid, Event event);

    public Event consumerConsumeEvent(String consumer_uuid);
}
