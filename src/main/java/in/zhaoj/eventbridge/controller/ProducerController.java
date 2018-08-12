package in.zhaoj.eventbridge.controller;

import in.zhaoj.eventbridge.pojo.Event;
import in.zhaoj.eventbridge.pojo.Response;
import in.zhaoj.eventbridge.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @apiDefine productGroup 生产者相关
 */

/**
 * @author: jinzhao
 * @date:2018/8/12
 * @description:
 */
@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Autowired
    private EventService eventService;

    /**
    * @api {post} /producer/event 事件生产
    * @apiName ProducerEventProduct
    * @apiGroup productGroup
    * @apiVersion 1.0.0
    * @apiDescription 生产一个事件
    *
    * @apiUse header
    *
    * @apiParam {int} event_id 事件 ID
    * @apiParamExample {json} 请求示例
    * {
    *      "event_id": 1
    * }
    *
    * @apiSuccess (请求成功) {json} code 结果码
    * @apiSuccessExample 请求成功
    * {
    *      code: 100
    * }
    *
    */
    @RequestMapping(value = "/event", method = RequestMethod.POST)
    public Response productEvent(@RequestBody Event event) {
        this.eventService.producerProductEvent(event);

        Response response = new Response(Response.CODE_SUCCESS);
        return response;
    }
}
