package in.zhaoj.eventbridge.controller;

import in.zhaoj.eventbridge.pojo.Event;
import in.zhaoj.eventbridge.pojo.Response;
import in.zhaoj.eventbridge.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    * @api {post} /producer/event?consumer_uuid=:consumer_uuid 事件生产
    * @apiName ProducerEventProduct
    * @apiGroup productGroup
    * @apiVersion 2.0.0
    * @apiDescription 生产一个事件
    *
    * @apiUse header
    *
    * @apiParam {string} consumer_uuid 消费者 UUID
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
    public Response productEvent(@RequestParam String consumer_uuid, @RequestBody Event event) {
        this.eventService.producerProductEvent(consumer_uuid, event);

        Response response = new Response(Response.CODE_SUCCESS);
        return response;
    }
}
