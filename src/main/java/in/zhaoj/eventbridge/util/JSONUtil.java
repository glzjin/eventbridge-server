package in.zhaoj.eventbridge.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

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

    public static HashMap<String, Object> decode(String json_string) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        HashMap<String, Object> map = new HashMap<String, Object>();

        // convert JSON string to Map
        map = mapper.readValue(json_string, new TypeReference<HashMap<String, Object>>(){});

        return map;
    }
}
