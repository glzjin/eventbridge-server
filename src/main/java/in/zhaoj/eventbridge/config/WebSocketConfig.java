package in.zhaoj.eventbridge.config;

import in.zhaoj.eventbridge.websocket.ConsumerWebsocket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author: jinzhao
 * @date:2018/8/15
 * @description:
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(consumerWebsocket(), "/consumer/websocket").setAllowedOrigins("*");
    }

    @Bean
    public ConsumerWebsocket consumerWebsocket()
    {
        return new ConsumerWebsocket();
    }
}
