package org.xueliang.commons.support.sms.alidayu;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;

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
        boolean result = sender.send("131xxxx8910", "短信签名", "SMS_模版代码", new LinkedHashMap<String, String>() {
            {
                put("captcha", "123456");
            }
        });
        Assert.state(result, "发送失败");
    }
}
