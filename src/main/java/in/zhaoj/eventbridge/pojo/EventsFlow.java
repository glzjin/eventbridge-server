package in.zhaoj.eventbridge.pojo;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author: jinzhao
 * @date:2018/8/14
 * @description:事件流
 */
@Component
public class EventsFlow {
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Event>> events;

    public EventsFlow() {
        this.events = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Event>>();
    }

    public void productEvent(String consumer_uuid, Event event) {
        ConcurrentLinkedQueue<Event> queue = events.get(consumer_uuid);

        if(queue == null) {
            events.put(consumer_uuid, new ConcurrentLinkedQueue<Event>());
            queue = events.get(consumer_uuid);
        }

        queue.offer(event);
    }

    public Event consumeEvent(String consumer_uuid) {
        ConcurrentLinkedQueue<Event> queue = events.get(consumer_uuid);

        if(queue == null) {
            return null;
        }

        if(queue.size() == 0) {
            return null;
        }

        Event event = queue.poll();
        return event;
    }
}
