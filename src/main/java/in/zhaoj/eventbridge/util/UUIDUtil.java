package in.zhaoj.eventbridge.util;

import java.util.UUID;

/**
 * @author: jinzhao
 * @date:2018/8/14
 * @description:UUID工具类
 */
public class UUIDUtil {
    public static String genUUID(){

        return UUID.randomUUID().toString().replace("-", "").toLowerCase();

    }
}
