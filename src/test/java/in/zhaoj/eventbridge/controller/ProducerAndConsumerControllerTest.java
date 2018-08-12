package in.zhaoj.eventbridge.controller;

import in.zhaoj.eventbridge.pojo.Response;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerAndConsumerControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Value("${system.consumer_key}")
    private String producer_key;

    @Value("${system.producer_key}")
    private String consumer_key;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }


    public void producerProductEvent() throws Exception {
        JSONObject param = new JSONObject() ;
        param.put("event_id", 1);
        String json = param.toString() ;

        RequestBuilder request = MockMvcRequestBuilders.post("/producer/event")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header("Key", producer_key)
                .content(json) ;

        MvcResult mvcResult = mockMvc.perform(request).andReturn() ;
        String content = mvcResult.getResponse().getContentAsString();

        Assert.assertEquals("{\"code\":" + Response.CODE_SUCCESS + "}", content);
    }

    public String consumeEvent() throws Exception {
        JSONObject param = new JSONObject() ;
        param.put("userId", "");
        String json = param.toString() ;

        RequestBuilder request = MockMvcRequestBuilders.get("/consumer/event")
                .header("Key", producer_key);

        MvcResult mvcResult = mockMvc.perform(request).andReturn() ;
        String content = mvcResult.getResponse().getContentAsString();

        return content;
    }

    public void consumerConsumeEvent() throws Exception {
        String content = consumeEvent();

        Assert.assertEquals("{\"code\":" + Response.CODE_SUCCESS+ ",\"data\":{\"event\":{\"event_id\":1}}}", content);
    }

    public void consumerConsumeEventBumpNullEvent() throws Exception {
        String content = consumeEvent();

        Assert.assertEquals("{\"code\":" + Response.CODE_SUCCESS_BUT_NULL + "}", content);
    }

    @Test
    public void test() throws Exception {
        producerProductEvent();
        consumerConsumeEvent();
        consumerConsumeEventBumpNullEvent();
    }
}