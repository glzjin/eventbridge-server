package in.zhaoj.eventbridge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @apiDefine header
 * @apiHeader {string} Key 系统里设定的对应角色的 Key
 * @apiHeaderExample {string} 头示例:
 * Key:123
 */

@SpringBootApplication
public class EventbridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventbridgeApplication.class, args);
    }
}
