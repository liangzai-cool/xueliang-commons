package org.xueliang.commons.support.sms.alidayu;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * @author XueLiang
 * @date 2018/9/2 23:21
 */
public class AliDayuSmsSenderTest {

    private static AliDayuSmsSender sender;

    @BeforeClass
    public static void beforeClass() throws Exception {
        String accessKeyId = "";
        String accessKeySecret = "";
        sender = new AliDayuSmsSender(accessKeyId, accessKeySecret);
    }

    @Test
    public void testSend() throws Exception {
        JSONObject json = new JSONObject();
        json.put("captcha", "123456");
        boolean result = sender.send("131xxxx8910", "短信签名", "SMS_模版代码", json.toString());
        Assert.state(result, "发送失败");
    }
}
