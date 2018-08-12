package in.zhaoj.eventbridge.pojo;

import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author: jinzhao
 * @date:2018/8/12
 * @description:事件流
 */
@Component
public class EventsFlow {
    private Queue<Event> events;

    public EventsFlow() {
        this.events = new ConcurrentLinkedQueue<Event>();
    }

    public void productEvent(Event event) {
        events.offer(event);
    }

    public Event consumeEvent() {
        if(events.size() == 0) {
            return null;
        }

        Event event = events.poll();
        return event;
    }
}
