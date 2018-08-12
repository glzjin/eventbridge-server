package in.zhaoj.eventbridge.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author: jinzhao
 * @date:2018/8/12
 * @description:
 */
public class JSONUtil {
    public static String encode(Object map) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writer().writeValueAsString(map);
    }
}
