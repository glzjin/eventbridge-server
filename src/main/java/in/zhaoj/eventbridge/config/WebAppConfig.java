package in.zhaoj.eventbridge.config;

import in.zhaoj.eventbridge.interceptor.ConsumerInterceptor;
import in.zhaoj.eventbridge.interceptor.ProducerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: jinzhao
 * @date:2018/8/12
 * @description:配置
 */
@Configuration
public class WebAppConfig implements WebMvcConfigurer {
    /**
     * 为了依赖注入
     * @return
     */
    @Bean
    public ProducerInterceptor producerInterceptor() {
        return new ProducerInterceptor();
    }

    @Bean
    public ConsumerInterceptor consumerInterceptor() {
        return new ConsumerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 多个拦截器组成一个拦截器链
        registry.addInterceptor(this.producerInterceptor()).addPathPatterns("/producer/**");
        registry.addInterceptor(this.consumerInterceptor()).addPathPatterns("/consumer/**");
    }
}
