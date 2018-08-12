package in.zhaoj.eventbridge.util;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author: jinzhao
 * @date:2018/8/12
 * @description:
 */
public class SendMsgUtil {
    /**
     * 将某个对象转换成json格式并发送到客户端
     * @param response
     * @param obj
     * @throws Exception
     */
    public static void sendJsonMessage(HttpServletResponse response, Object obj) throws Exception {
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSONUtil.encode(obj));
        writer.close();
        response.flushBuffer();
    }
}
