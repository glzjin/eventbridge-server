package in.zhaoj.eventbridge.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;

/**
 * @author: jinzhao
 * @date:2018/8/12
 * @description:返回信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    public final static int CODE_SUCCESS = 100;
    public final static int CODE_SUCCESS_BUT_NULL = 101;
    public final static int CODE_KEY_ERROR = 201;

    private int code;

    private HashMap<String, Object> data;

    public Response(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setData(String key, Object value) {
        if(data == null) {
            data = new HashMap<String, Object>();
        }

        data.put(key, value);
    }
}
