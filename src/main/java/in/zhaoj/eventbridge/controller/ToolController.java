package in.zhaoj.eventbridge.controller;

import in.zhaoj.eventbridge.pojo.Response;
import in.zhaoj.eventbridge.util.UUIDUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @apiDefine toolGroup 工具
 * @author: jinzhao
 * @date:2018/8/14
 * @description:
 */
@RestController
@RequestMapping("/tool")
public class ToolController {
    /**
     * @api {get} /tool/uuid   生成 UUID
     * @apiName  genUUID
     * @apiGroup toolGroup
     * @apiVersion 2.0.0
     * @apiDescription 获取 一个 UUID
     *
     * @apiSuccess (请求成功) {json} code 结果码
     * @apiSuccess (请求成功) {json} data 数据
     * @apiSuccessExample 请求成功
     * {
     *      code: 100,
     *         "data": {
     *         "uuid": "*****"
     *     }
     * }
     *
     */
    @RequestMapping("/uuid")
    public Response uuid() {
        Response response = new Response(100);
        response.setData("uuid", UUIDUtil.genUUID());
        return response;
    }
}
