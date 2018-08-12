package in.zhaoj.eventbridge.interceptor;

import in.zhaoj.eventbridge.pojo.Response;
import in.zhaoj.eventbridge.util.SendMsgUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: jinzhao
 * @date:2018/8/12
 * @description: 生产者拦截器
 */
public class ProducerInterceptor implements HandlerInterceptor {
    @Value("${system.producer_key}")
    private String producer_key;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(request.getMethod().equals("OPTIONS")) {
            return true;
        }

        //Http 头匹配
        String key = request.getHeader("Key");
        if(key != null) {
            if(key.equals(producer_key)) {
                return true;
            }
        }

        //URL  参数匹配
        String url_key = request.getParameter("key");
        if(url_key != null) {
            if(url_key.equals(producer_key)) {
                return true;
            }
        }

        SendMsgUtil.sendJsonMessage(response, new Response(Response.CODE_KEY_ERROR));
        return false;
    }
}
