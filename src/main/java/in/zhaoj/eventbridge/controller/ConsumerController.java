package in.zhaoj.eventbridge.controller;

import in.zhaoj.eventbridge.pojo.Event;
import in.zhaoj.eventbridge.pojo.Response;
import in.zhaoj.eventbridge.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @apiDefine consumeGroup 消费者相关
 */
/**
 * @author: jinzhao
 * @date:2018/8/12
 * @description:
 */
@RestController
@RequestMapping("/consumer/{consumer_uuid}")
public class ConsumerController {

    @Autowired
    private EventService eventService;

    /**
    * @api {get} /consumer/:consumer_uuid/event 事件消费
    * @apiName ConsumerEventConsume
    * @apiGroup consumeGroup
    * @apiVersion 2.0.0
    * @apiDescription 获取并消费一个事件
    *
    * @apiUse header
    *
    * @apiParam {string} consumer_uuid 消费者 UUID
    * @apiSuccess (请求成功) {json} code 结果码
    * @apiSuccess (请求成功) {json} data 数据
    * @apiSuccessExample 请求成功
    * {
    *      code: 100,
    *         "data": {
    *           "event": {
    *               "event_id": 1
    *           }
    *     }
    * }
    *
    *  @apiError (请求失败) {json} code 错误码
    *  @apiErrorExample {json} 当前无事件
    *  {
    *      code: 101
    *  }
    *
    */
    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public Response productEvent(@PathVariable String consumer_uuid) {
        Event event = this.eventService.consumerConsumeEvent(consumer_uuid);

        Response response = new Response(Response.CODE_SUCCESS);
        if(event != null) {
            response.setData("event", event);
        } else {
            response.setCode(Response.CODE_SUCCESS_BUT_NULL);
        }

        return response;
    }
}
